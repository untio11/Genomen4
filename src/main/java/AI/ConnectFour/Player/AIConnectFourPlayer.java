package AI.ConnectFour.Player;

import AI.ConnectFour.PlayConnectFour;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

public class AIConnectFourPlayer extends ConnectFourPlayer {

    private static final int seed = 1234;

    protected MultiLayerNetwork net;

    public AIConnectFourPlayer() {
        super();
    }

    @Override
    public void init() {
        this.createNetwork();
    }

    protected void createNetwork() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .seed(seed)
                .biasInit(0)
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(game.boardSize()*game.boardSize())
                        .nOut(game.boardSize()*game.boardSize())
                        .activation(Activation.SIGMOID)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
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

    }

    @Override
    public void performMove(int index) {

    }
}
