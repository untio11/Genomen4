package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.*;

/**
 * Class for training an AI player against other static players. The AI players will use a genetic algorithm
 * @param <A> The AI player class
 * @param <B> The normal (static) player class
 * @param <G> The game class
 */
public abstract class SingleBiAIGameTrainer<A extends TrainerAIPlayer, B, G> extends BaseAIGameTrainer<G> {

    private List<A> players1;
    private List<B> players2;

    private List<Pair<A, B>> competition;

    private Map<A, Integer> playerScores1;

    private GeneticAlgorithm<A> geneticAlgorithm1;

    public SingleBiAIGameTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
        players1 = new ArrayList<>();
        players2 = new ArrayList<>();
        playerScores1 = new HashMap<>();

        final SingleBiAIGameTrainer<A, B, G> trainer = this;

        geneticAlgorithm1 = new GeneticAlgorithm<>(new AIPlayerBuilder<A>() {

            @Override
            public A createPlayer(Map<String, INDArray> paramTable) {
                return trainer.createPlayer1(paramTable);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.createPlayers1(players1);
        this.createPlayers2(players2);
    }

    @Override
    protected void setupCompetition() {
        competition = this.createCompetition(players1, players2);
    }

    @Override
    protected void playCompetition() {
        for (Pair<A, B> players : competition) {
            G game = this.createGame(players);
            this.playGame(game);
        }
    }

    @Override
    protected void evaluateCompetition() {
        // Get the best player from group 1
        LinkedHashMap<A, Integer> sortedPlayers1 = evaluatePlayers(playerScores1);
        Map.Entry<A, Integer> bestPlayer1Entry = sortedPlayers1.entrySet().iterator().next();

        Date date = new Date();

        // Save the best player from group 1
        String fileName1 = date.getTime() + "-" + this.getName() + "-1" + "-" + bestPlayer1Entry.getValue();
        this.savePlayer(bestPlayer1Entry.getKey(), fileName1);

        // Compute the average scores
        int sum1 = 0;
        for (Map.Entry<A, Integer> entry : sortedPlayers1.entrySet()) {
            sum1 += entry.getValue();
        }

        double avg1 = sum1 * 1f / sortedPlayers1.size();

        System.out.print("Best Scores: \t" + bestPlayer1Entry.getValue());

        System.out.println("\t Average Scores: \t" + avg1);
    }

    @Override
    protected void performGeneticEvolution() {
        // Genetically evolve player group 1
        LinkedHashMap<A, Integer> sortedPlayers1 = evaluatePlayers(playerScores1);
        players1 = geneticAlgorithm1.performPlayerEvolution(sortedPlayers1);
    }

    @Override
    protected void resetGeneticAlgorithm() {
        // Reset player group 1
        playerScores1 = new HashMap<>();
        for (A player : players1) {
            playerScores1.put(player, 0);
        }
    }

    protected void createPlayers1(List<A> players1) {
        for (int i = 0; i < this.nPlayers; i++) {
            A player = this.createPlayer1();
            players1.add(player);
            playerScores1.put(player, 0);
        }
    }

    public void setResults1(A player, int score) {
        int oldScore = playerScores1.get(player);
        playerScores1.put(player, oldScore + score);
    }

    public LinkedHashMap<A, Integer> getScoredPlayers1() {
        return evaluatePlayers(playerScores1);
    }

    protected abstract A createPlayer1();

    protected abstract A createPlayer1(Map<String, INDArray> paramTable);

    protected abstract void createPlayers2(List<B> players);

    protected abstract B createPlayer2();

    protected abstract B createPlayer2(Map<String, INDArray> paramTable);

    protected abstract List<Pair<A, B>> createCompetition(List<A> players1, List<B> players2);

    protected abstract G createGame(Pair<A, B> players);
}
