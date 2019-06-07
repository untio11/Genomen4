package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.io.File;
import java.util.*;

public abstract class AIGameTrainer<A extends TrainerAIPlayer, G> extends BaseAIGameTrainer<G> {

    private List<A> players;
    private List<Pair<A, A>> competition;
    private Map<A, Integer> playerScores;
    private GeneticAlgorithm<A> geneticAlgorithm;

    public AIGameTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
        players = new ArrayList<>();
        playerScores = new HashMap<>();

        final AIGameTrainer<A, G> trainer = this;

        geneticAlgorithm = new GeneticAlgorithm<>(new AIPlayerBuilder<A>() {
            @Override
            public A createPlayer(Map<String, INDArray> paramTable) {
                return trainer.createPlayer(paramTable);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.createPlayers(players);
    }

    @Override
    protected void setupCompetition() {
        competition = this.createCompetition(players);
    }

    @Override
    protected void playCompetition() {
        for (Pair<A, A> players : competition) {
            G game = this.createGame(players);
            this.playGame(game);
        }
    }

    @Override
    protected void evaluateCompetition() {
        int bestScore = 0;
        A bestPlayer = null;
        for (A player : players) {
            int pScore = playerScores.get(player);
            if (pScore > bestScore || bestPlayer == null) {
                bestPlayer = player;
                bestScore = pScore;
            }
        }

        Date date = new Date();

        String fileName = date.getTime() + "-" + this.getName() + "-" + bestScore;
        this.savePlayer(bestPlayer, fileName);

        System.out.println("Best Score: " + bestScore);
    }

    @Override
    protected void performGeneticEvolution() {
        LinkedHashMap<A, Integer> sortedPlayers = evaluatePlayers(playerScores);
        players = geneticAlgorithm.performPlayerEvolution(sortedPlayers);
    }

    @Override
    protected void resetGeneticAlgorithm() {
        playerScores = new HashMap<>();
        for (A player : players) {
            playerScores.put(player, 0);
        }
    }

    @Override
    protected void saveStatistics(File f) {
        // TODO: implement the statistics saving
    }

    protected void createPlayers(List<A> players) {
        for (int i = 0; i < this.nPlayers; i++) {
            A player = this.createPlayer();
            players.add(player);
            playerScores.put(player, 0);
        }
    }

    public List<A> getPlayers() {
        return players;
    }

    public List<Pair<A, A>> getCompetition() {
        return competition;
    }

    public void setResults(A player, int score) {
        int oldScore = playerScores.get(player);
        playerScores.put(player, oldScore + score);
    }

    public LinkedHashMap<A, Integer> getScoredPlayers() {
        return evaluatePlayers(playerScores);
    }

    protected abstract A createPlayer();

    protected abstract A createPlayer(Map<String, INDArray> paramTable);

    protected abstract List<Pair<A, A>> createCompetition(List<A> players);

    protected abstract G createGame(Pair<A, A> players);
}
