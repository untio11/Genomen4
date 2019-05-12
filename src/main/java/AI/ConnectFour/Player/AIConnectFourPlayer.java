package AI.ConnectFour.Player;

import AI.Trainer.TrainerAIPlayer;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class AIConnectFourPlayer extends ConnectFourPlayer implements TrainerAIPlayer {

    private static final int seed = 1234;

    protected MultiLayerNetwork net;

    public AIConnectFourPlayer() {
        super();
    }

    @Override
    public void init() {
        if (this.net == null) {
            this.createNetwork();
        }
    }

    protected void createNetwork() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .biasInit(0)
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(game.boardSize()*game.boardSize())
                        .nOut(game.boardSize()*game.boardSize())
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.L2)
                        .nOut(game.boardSize()*game.boardSize())
                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.DISTRIBUTION)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .build();

        net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Override
    public void requestMove(int[][] board) {

        // Determine the placement by the AI with the board as input
        int index = this.determinePlace(board);

        int y = index / game.boardSize();
        int x = index % game.boardSize();

        if (game.checkMove(x, y)) {
            game.doMove(x, y, player);
        } else {
            // The AI player will place at a random position if the chosen position is invalid
            this.placeRandom();
        }
    }

    private int determinePlace(int[][] board) {
        INDArray input = this.boardToINDArray(board);
        INDArray output = net.output(input);

        INDArray max = output.argMax(1);

        int maxIndex = max.getInt(0);

        return maxIndex;
    }

    private INDArray boardToINDArray(int[][] board) {
        float[] processedBoard = this.processBoard(board);
        INDArray input2 = Nd4j.create(processedBoard, new int[]{1, game.boardSize()*game.boardSize()});
        return input2;
    }

    private float[] processBoard(int[][] board) {
        float[] processed = new float[game.boardSize()*game.boardSize()];
        for (int y = 0; y < game.boardSize(); y++) {
            for (int x = 0; x < game.boardSize(); x++) {
                int i = y * game.boardSize() + x;
                processed[i] = this.normalizeBoardValue(board[y][x]);
            }
        }
        return processed;
    }

    private float normalizeBoardValue(int value) {
        switch (value) {
            case 1:
                return -1f;
            case 2:
                return 1f;
            default:
                return 0f;
        }
    }

    private void placeRandom() {
        Random r = new Random();
        int x = r.nextInt(game.boardSize());
        int y = r.nextInt(game.boardSize());

        while (!game.checkMove(x, y)) {
            x = r.nextInt(game.boardSize());
            y = r.nextInt(game.boardSize());
        }

        game.doMove(x, y, player);
    }

    @Override
    public void performMove(int index) {

    }

    @Override
    public MultiLayerNetwork getNetwork() {
        return this.net;
    }

    @Override
    public void saveNetwork(File f) throws IOException {
        ModelSerializer.writeModel(this.net, f, false);
    }

    @Override
    public MultiLayerNetwork loadNetwork(File f) throws IOException {
        this.net = ModelSerializer.restoreMultiLayerNetwork(f, false);
        return this.net;
    }
}
