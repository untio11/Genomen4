package AI.Genomen.Player;

import AI.Trainer.TrainerAIPlayer;
import Engine.Controller.AIController;
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

public class AIGenomenPlayer extends AIController implements TrainerAIPlayer {

    // The number of frames between every update of the ai player
    protected final int updateFrequency = 30;

    // The number of inputs of the neural network
    protected final int inputCount = 8;

    // Add the player position to the input
    protected final boolean addPosition = true;

    // The number of outputs of the neural network
    protected final int outputCount = 2;

    // The number of values that the neural network should remember
    // These will be passed through in the next iteration
    protected final int rememberCount = 3;

    // The maximum length of each ray coming from the player
    protected final int maxRayLength = 6;

    // The current number of frames elapsed since the last update
    protected int frame = 0;

    protected MultiLayerNetwork net;

    private double[] rememberInputs;

    public AIGenomenPlayer() {
        rememberInputs = new double[getRememberCount()];
    }

    public void init() {
        if (this.net == null) {
            this.createNetwork();
        }
    }

    @Override
    public void update(double dt) {
        // First check if the AI should update the movement axes
        if (frame > getUpdateFrequency()) {
            // If so, update these
            this.movePlayer();
            frame = 0;
        }

        // Next, update the AI controller
        super.update(dt);

        // Increase the frame counter
        frame++;
    }

    protected void movePlayer() {
        double[] position = new double[0];
        if (isAddPosition()) {
            position = this.getPosition();
        }

        double[][] input = this.getInput(getInputCount(), getMaxRayLength());
        // Process the input so the neural network can accept it
        INDArray indArray = this.inputToINDArray(position, input);

        // Evaluate the network with the processed input
        INDArray output = this.evaluateNetwork(indArray);

        // Process the output so that it can be used for moving the player
        double xAxis = output.getDouble(0, 0);
        double yAxis = output.getDouble(0, 1);

        // Set the movement axes of the player depending on the output
        this.setAxis(xAxis, yAxis);
    }

    private INDArray inputToINDArray(double[] position, double[][] input) {
        // Process the input array from a 2D double array to 1D double array and normalize values if necessary
        double[] processedInput = this.processInput(input);

        // adding the remembered inputs to the processed inputs
        int length = position.length + processedInput.length + getRememberCount();
        double[] netInput = new double[length];
        for (int i = 0; i < length; i++) {
            if (i < position.length) {
                netInput[i] = position[i];
            } else if (i < position.length + processedInput.length) {
                netInput[i] = processedInput[i - position.length];
            } else {
                netInput[i] = rememberInputs[i - position.length - processedInput.length];
            }
        }

        // Convert the processed inputs to an INDArray
        INDArray indArray = Nd4j.create(netInput, new int[]{1, netInput.length});
        return indArray;
    }

    private double[] processInput(double[][] input) {
        double[] processed = new double[(getInputCount() + 1) * 2 + 1];
        for (int i = 0; i < input.length; i++) {
            if (i == input.length - 1) {
                processed[i*2] = input[i][0];

                double magnitude = input[i][1];
                double angle = input[i][2];
                double angleOffset = 0;

                double xOffset = Math.cos(Math.toRadians(angle + angleOffset));
                double yOffset = Math.sin(Math.toRadians(angle + angleOffset));

                processed[i*2+1] = xOffset; //input[i][1];
                processed[i*2+2] = yOffset; //input[i][2];
                continue;
            }
            // Normalize the values and store them in the 1D array
            processed[i*2] = this.normalizeAccessible(input[i][0]);
            processed[i*2+1] = this.normalizeDistance(input[i][1]);
        }
        return processed;
    }

    private double normalizeAccessible(double input) {
        return input;
    }

    private double normalizeDistance(double input) {
        return input;
    }

    private INDArray evaluateNetwork(INDArray indArray) {
        // Evaluate the neural network with set input and return the output
        INDArray output = net.output(indArray);
        // set rememberInputs to the last part of the output
        for (int i = 0; i < getRememberCount(); i++) {
            rememberInputs[i] = output.getDouble(0, i + getOutputCount());
        }

        return output;
    }

    protected void createNetwork() {
        // TODO: Improve dummy network
        // TODO: Implement remembering in the network
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .biasInit(0)
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn((getInputCount() + 1) * 2 + 1 + getRememberCount() + getPositionCount())
                        .nOut(16)
                        .activation(Activation.RELU)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(10)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.L2)
                        .nOut(getOutputCount() + getRememberCount())
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
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

    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public int getInputCount() {
        return inputCount;
    }

    public boolean isAddPosition() {
        return addPosition;
    }

    public int getPositionCount() {
        return addPosition ? 2 : 0;
    }

    public int getOutputCount() {
        return outputCount;
    }

    public int getRememberCount() {
        return rememberCount;
    }

    public int getMaxRayLength() {
        return maxRayLength;
    }
}
