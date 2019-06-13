package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class GeneticAlgorithm<A extends TrainerAIPlayer> {

    private BaseAIGameTrainer<?>.AIPlayerBuilder<A> playerBuilder;

    // The crossover probability is the percentage of weights and biases that is taken from the other parent
    // 0.1 and 0.9 therefore have similar effects and 0.5 will take half of the weights of both parents
    private float crossoverProbability = 0.01f;

    // The mutate probability is the chance that a weight or bias is going to be mutated
    private float mutateProbability = 0.1f;
    // The mutate percentage is the magnitude by which the random mutation is multiplied
    // This mutation is added to the original weight
    // Should be a bit higher when the mutateCubicDistribution variable is true
    private float mutatePercentage = 1f;

    // With mutate cubic distribution, the random mutation values have a tendency to stay low with higher extremes
    // This could improve random mutations and avoid local minimums
    private boolean mutateCubicDistribution = true;

    // This percentage indicates the number of players that will be copied directly into the new iteration without
    // being changed by the genetic algorithm. Rounded up to the next integer
    private float copyParentPercentage = 0.10f;

    // When set to true, this boolean results in the best performing player always being copied into the new iteration
    // The copyParentPercentage should have a value above 0 to make this work.
    private boolean copyBestParent = true;

    public GeneticAlgorithm(BaseAIGameTrainer<?>.AIPlayerBuilder<A> playerBuilder) {
        this.playerBuilder = playerBuilder;
    }

    public List<A> performPlayerEvolution(LinkedHashMap<A, Integer> sortedPlayers) {
        LinkedHashMap<A, Integer> accumulatedPlayers = this.accumulatePlayerScores(sortedPlayers);
        int sum = 0;
        for (int value : sortedPlayers.values()) {
            sum += value;
        }
        List<A> newPlayers = new ArrayList<>();

        // Copy parents from old generation
        int numberOfParents = (int) Math.ceil(sortedPlayers.size() * copyParentPercentage);
        for (int i = 0; i < numberOfParents; i++) {
            A parent;
            if (i == 0 && copyBestParent) {
                // The first copied parent is always the best parent
                // This way, the best parent does not get lost
                parent = sortedPlayers.entrySet().iterator().next().getKey();
            } else {
                // For the other copied parents, select a parent from the sorted players
                parent = this.selectParent(sortedPlayers, sum);
            }
            A copyParent = playerBuilder.createPlayer(parent.getNetwork().paramTable());
            newPlayers.add(copyParent);
        }

        // Create new players by generating from previous iteration for every new player
        for (int i = newPlayers.size(); i < sortedPlayers.size(); i++) {
            // -> Selection
            // Select two random parents for the child with a higher probability of well performing players
            A parent1 = this.selectParent(sortedPlayers, sum);
            A parent2 = this.selectParent(sortedPlayers, sum);
            // There is a probability that both parents are equal but this should not matter too much

            // Create a new network for the new child

            // -> Crossover
            Map<String, INDArray> p1Table = parent1.getNetwork().paramTable();
            Map<String, INDArray> p2Table = parent2.getNetwork().paramTable();
            Map<String, INDArray> childTable = this.crossover(p1Table, p2Table);

            // -> Mutation
            this.mutate(childTable);

            A childPlayer = playerBuilder.createPlayer(childTable);

            newPlayers.add(childPlayer);
        }

        return newPlayers;
    }

    public LinkedHashMap<A, Integer> accumulatePlayerScores(LinkedHashMap<A, Integer> sortedPlayers) {
        LinkedHashMap<A, Integer> accPlayers = new LinkedHashMap<>();

        List<A> reverseOrderedKeys = new ArrayList<>(sortedPlayers.keySet());
        Collections.reverse(reverseOrderedKeys);
        int acc = 0;
        for (A key : reverseOrderedKeys) {
            int value = sortedPlayers.get(key);
            acc += value;
            accPlayers.put(key, acc);
        }

        return accPlayers;
    }

    private double convertValue(double x) {
        return x * x;
    }

    private A selectParent(LinkedHashMap<A, Integer> players, int sum) {
        Random r = new Random();
//        int v = r.nextInt(Math.max(1, sum));

        int v = (int) (convertValue(r.nextDouble()) * sum);
        Iterator<Map.Entry<A, Integer>> iterator = players.entrySet().iterator();
        A parent = null;
        while(v >= 0 && iterator.hasNext()) {
            Map.Entry<A, Integer> player = iterator.next();
            parent = player.getKey();
            v -= player.getValue();
        }
        return parent;
    }

    private Map<String, INDArray> crossover(Map<String, INDArray> m1, Map<String, INDArray> m2) {
        Map<String, INDArray> mChild = new HashMap<>();
        for (String key : m1.keySet()) {
            INDArray a1 = m1.get(key);
            INDArray a2 = m2.get(key);
            INDArray aChild = crossoverINDArray(a1, a2);
            mChild.put(key, aChild);
        }
        return mChild;
    }

    private INDArray crossoverINDArray(INDArray a1, INDArray a2) {
        INDArray mask = Nd4j.zeros(a1.shape());
        createRandomMask(mask, crossoverProbability);
        return a1.putWhereWithMask(mask, a2);
    }

    private void mutate(Map<String, INDArray> paramTable) {
        for (String key : paramTable.keySet()) {
            this.mutateINDArray(paramTable.get(key));
        }
    }

    private void mutateINDArray(INDArray a) {
        INDArray mask = Nd4j.zeros(a.shape());
        createRandomMask(mask, mutateProbability);
        INDArray rand = Nd4j.rand(a.shape());
        if (mutateCubicDistribution) {
            rand.subi(0.5);
            rand = rand.mul(rand).mul(rand);
            rand.muli(8);
        } else {
            rand.muli(2).subi(1);
        }

        rand.muli(mutatePercentage);
        a.addi(rand);
    }

    private INDArray createRandomMask(INDArray mask, float probability) {
        INDArray source = Nd4j.create(new float[] {0, 1});
        INDArray probs = Nd4j.create(new float[] {1 - probability, probability});
        Nd4j.choice(source, probs, mask);
        return mask;
    }
}
