package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class BaseAIGameTrainer<G> {

    protected final int nPlayers;
    protected final int iterations;

    public BaseAIGameTrainer(int nPlayers, int iterations) {
        this.nPlayers = nPlayers;
        this.iterations = iterations;
    }

    protected void savePlayer(TrainerAIPlayer player, String fileName) {
        File f = new File("res/network/" + fileName + ".net");

        try {
            player.saveNetwork(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T> LinkedHashMap<T, Integer> evaluatePlayers(Map<T, Integer> playerScores) {
        // Sort the players based on the score
        return playerScores.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void init() {

    }

    public void runGeneticAlgorithm() {
        // Assume that the trainer has already been initialized and that players and a competition exist
        // Iterate over the genetic algorithm for a specified number of iterations
        for (int i = 0; i < this.iterations; i++) {
            // Create the competition
            this.setupCompetition();

            // Run the competition
            this.playCompetition();

            // Evaluate the competition and possibly save some networks
            this.evaluateCompetition();

            // Skip generating new players if this is the last iteration
            if (i + 1 == this.iterations) {
                continue;
            }

            // Determine the best players and perform evolution
            this.performGeneticEvolution();

            // Reset the local variables for the genetic algorithm
            this.resetGeneticAlgorithm();
        }

        // Save the best players so that they can be used again later

        // Save the statistics of the training
        Date date = new Date();
        String fileName = date.getTime() + "-" + this.getName() + "-stats";
        File csv = new File("res/network/" + fileName + ".csv");
        this.saveStatistics(csv);
    }

    protected void saveCsv(List<List<?>> rows, File f) {
        try {
            FileWriter csvWriter = new FileWriter(f);

            for (List<?> rowData : rows) {
                csvWriter.append(this.listToString(rowData));
                csvWriter.append("\n");
            }

            csvWriter.flush();
            csvWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String listToString(List<?> list) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iter = list.iterator();
        while(iter.hasNext()) {
            builder.append(iter.next());
            if (iter.hasNext()) {
                builder.append(",");
            }
        }
        return builder.toString();
    }

    protected abstract String getName();

    protected abstract void setupCompetition();

    protected abstract void playCompetition();

    protected abstract void evaluateCompetition();

    protected abstract void performGeneticEvolution();

    protected abstract void resetGeneticAlgorithm();

    protected abstract void saveStatistics(File f);

    protected abstract void playGame(G game);

    public abstract class AIPlayerBuilder<A extends TrainerAIPlayer> {

        public abstract A createPlayer(Map<String, INDArray> paramTable);

    }
}
