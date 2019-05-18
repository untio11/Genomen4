package AI.Genomen.Player;

import AI.ConnectFour.PlayConnectFour;
import AI.Trainer.TrainerAIPlayer;
import GameState.Entities.Actor;
import GameState.World;
import Graphics.Models.TexturedModel;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.joml.Vector3f;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.io.File;
import java.io.IOException;

public class AIGenomenPlayer extends Actor implements TrainerAIPlayer {

    protected MultiLayerNetwork net;


    public AIGenomenPlayer() {
        super(null, null, 0, null, null, 1f, false);
    }

    /**
     * Initialize a player with the appropriate properties
     *
     * @param world
     * @param model     The model that the player should have: We probably want to change this to some loose reference
     * @param size
     * @param position  The position of the player
     * @param rotation  The rotation of the model
     * @param scale     The size of the model (I think)
     * @param kidnapper
     */
    public AIGenomenPlayer(World world, TexturedModel model, float size, Vector3f position, Vector3f rotation, float scale, boolean kidnapper) {
        super(world, model, size, position, rotation, scale, kidnapper);
    }

    public void init() {
        if (this.net == null) {
            this.createNetwork();
        }
    }

    protected void createNetwork() {
        // TODO: Improve dummy network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .biasInit(0)
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(PlayConnectFour.SIZE*PlayConnectFour.SIZE)
                        .nOut(PlayConnectFour.SIZE*PlayConnectFour.SIZE)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.L2)
                        .nOut(PlayConnectFour.SIZE*PlayConnectFour.SIZE)
                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.DISTRIBUTION)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .build();

        net = new MultiLayerNetwork(conf);
        net.init();
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
