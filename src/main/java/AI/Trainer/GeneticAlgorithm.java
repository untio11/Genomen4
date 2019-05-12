package AI.Trainer;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.*;

public class GeneticAlgorithm<A extends TrainerAIPlayer> {

    private AIGameTrainer<A, ?> trainer;

    public GeneticAlgorithm(AIGameTrainer<A, ?> trainer) {
        this.trainer = trainer;
    }

    public List<A> performPlayerEvolution(LinkedHashMap<A, Integer> sortedPlayers) {
        LinkedHashMap<A, Integer> accumulatedPlayers = this.accumulatePlayerScores(sortedPlayers);
        int sum = 0;
        for (int value : sortedPlayers.values()) {
            sum += value;
        }
        List<A> newPlayers = new ArrayList<>();
        // Create new players by generating from previous iteration for every new player
        for (int i = 0; i < sortedPlayers.size(); i++) {
            // -> Selection
            // Select two random parents for the child with a higher probability of well performing players
            A parent1 = this.selectParent(accumulatedPlayers, sum);
            A parent2 = this.selectParent(accumulatedPlayers, sum);
            // There is a probability that both parents are equal but this should not matter too much

            // Create a new network for the new child

            // -> Crossover
            Map<String, INDArray> p1Table = parent1.getNetwork().paramTable();
            Map<String, INDArray> p2Table = parent2.getNetwork().paramTable();
            Map<String, INDArray> childTable = this.crossover(p1Table, p2Table);

            // -> Mutation
            // TODO: Finish mutation

            A childPlayer = trainer.createPlayer(childTable);

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

    private A selectParent(LinkedHashMap<A, Integer> players, int sum) {
        Random r = new Random();
        int v = r.nextInt(sum);
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
        createRandomMask(mask);
        return a1.putWhereWithMask(mask, a2);
    }

    private INDArray createRandomMask(INDArray mask) {
        INDArray source = Nd4j.create(new float[] {0, 1});
        INDArray probs = Nd4j.create(new float[] {0.5f, 0.5f});
        Nd4j.choice(source, probs, mask);
        return mask;
    }
}
