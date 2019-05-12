package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AIGameTrainer<A extends TrainerAIPlayer, B> {

    private List<A> players;
    private List<Pair<A, A>> competition;
    private Map<A, Integer> playerScores;
    private GeneticAlgorithm<A> geneticAlgorithm;

    private final int nPlayers;
    private final int iterations;


    public AIGameTrainer(int nPlayers, int iterations) {
        this.nPlayers = nPlayers;
        this.iterations = iterations;
        players = new ArrayList<>();
        playerScores = new HashMap<>();
        geneticAlgorithm = new GeneticAlgorithm<>(this);
    }

    public void init() {
        this.createPlayers(players);
    }

    public void runGeneticAlgorithm() {
        // Assume that the trainer has already been initialized and that players and a competition exist
        // Iterate over the genetic algorithm for a specified number of iterations
        for (int i = 0; i < this.iterations; i++) {
            // Create the competition
            competition = this.createCompetition(players);

            // Run the competition
            this.playCompetition();

            // Determine the best players
            LinkedHashMap<A, Integer> sortedPlayers = this.evaluatePlayers();

            // Skip generating new players if this is the last iteration
            if (i + 1 == this.iterations) {
                continue;
            }

            players = geneticAlgorithm.performPlayerEvolution(sortedPlayers);
            playerScores = new HashMap<>();
            for (A player : players) {
                playerScores.put(player, 0);
            }
        }

        // Save the best players so that they can be used again later


    }

    protected void playCompetition() {
        for (Pair<A, A> players : competition) {
            B game = this.createGame(players);
            this.playGame(game);
        }

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

    public LinkedHashMap<A, Integer> evaluatePlayers() {
        // Sort the players based on the score
         return playerScores.entrySet().stream()
                .sorted((Map.Entry.<A, Integer>comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
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

    protected void savePlayer(A player, String fileName) {
        File f = new File("res/" + fileName + ".net");

        try {
            player.saveNetwork(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected abstract String getName();

    protected abstract A createPlayer();

    protected abstract A createPlayer(Map<String, INDArray> paramTable);

    protected abstract List<Pair<A, A>> createCompetition(List<A> players);

    protected abstract B createGame(Pair<A, A> players);

    protected abstract void playGame(B game);
}
