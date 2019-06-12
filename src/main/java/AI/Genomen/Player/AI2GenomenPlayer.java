package AI.Genomen.Player;

import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Arrays;

public class AI2GenomenPlayer extends AIGenomenPlayer {

    // The number of frames between every update of the ai player
    protected final int updateFrequency = 30;

    // The number of inputs of the neural network
    protected final int inputCount = 4;

    // Add the player position to the input
    protected final boolean addPosition = true;

    // The number of outputs of the neural network
    protected final int outputCount = 2;

    // Add the player boost to the output
    protected final boolean addBoost;

    // The number of values that the neural network should remember
    // These will be passed through in the next iteration
    protected final int rememberCount = 2;

    // The maximum length of each ray coming from the player
    protected final int maxRayLength = 6;

    public AI2GenomenPlayer() {
        super();
        addBoost = false;
    }

    public AI2GenomenPlayer(boolean boost) {
        super(boost);
        addBoost = boost;
    }

    @Override
    protected void movePlayer() {
        double[] position = new double[0];
        if (isAddPosition()) {
            position = this.getPosition();
        }

        double[] opponentPos = this.getOpponentPosition();

        double[][] input = this.getInput(getInputCount(), getMaxRayLength());
        // Process the input so the neural network can accept it
        INDArray indArray = this.inputToINDArray(input, position, opponentPos);

        // Evaluate the network with the processed input
        INDArray output = this.evaluateNetwork(indArray);

        // Process the output so that it can be used for moving the player
        double xAxis = output.getDouble(0, 0);
        double yAxis = output.getDouble(0, 1);

        // Get the boost value if necessary
        if (isAddBoost()) {
            this.setBoost(output.getDouble(0, getOutputCount()) > 0.5);
        }

        // Set the movement axes of the player depending on the output
        this.setAxis(xAxis, yAxis);
    }

    protected INDArray inputToINDArray(double[][] input, double[] pos, double[] opPos) {
        // Process the input array from a 2D double array to 1D double array and normalize values if necessary
        double[] processedInput = this.processInput(input);

        // adding the remembered inputs to the processed inputs
        int length = pos.length + opPos.length + processedInput.length + getRememberCount();
        double[] netInput = new double[length];
        for (int i = 0; i < length; i++) {
            if (i < processedInput.length) {
                netInput[i] = processedInput[i];
            } else if (i < processedInput.length + pos.length) {
                netInput[i] = pos[i - processedInput.length];
            } else if (i < processedInput.length + pos.length + opPos.length) {
                netInput[i] = opPos[i - processedInput.length - pos.length];
            } else {
                netInput[i] = rememberInputs[i - processedInput.length - pos.length - opPos.length];
            }
        }

        // Convert the processed inputs to an INDArray
        INDArray indArray = Nd4j.create(netInput, new int[]{1, netInput.length});
        return indArray;
    }

    protected double[] processInput(double[][] input) {
        double[] processed = new double[getInputCount() * 2];
        for (int i = 0; i < input.length - 1; i++) {
            // Normalize the values and store them in the 1D array
            processed[i*2] = this.normalizeAccessible(input[i][0]);
            processed[i*2+1] = this.normalizeDistance(input[i][1]);
        }
        return processed;
    }

    @Override
    protected void createNetwork() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .biasInit(0)
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(getInputCount() * 2 + getPositionCount() + getOpponentPositionCount() + getRememberCount())
                        .nOut(20)
                        .activation(Activation.RELU)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new DenseLayer.Builder()
                        .nOut(10)
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.L2)
                        .nOut(getOutputCount() + getBoostCount() + getRememberCount())
                        .activation(Activation.TANH)
                        .weightInit(new UniformDistribution(-1, 1))
                        .build())
                .build();

        net = new MultiLayerNetwork(conf);
        net.init();
    }

    @Override
    public int getUpdateFrequency() {
        return updateFrequency;
    }

    @Override
    public int getInputCount() {
        return inputCount;
    }

    @Override
    public boolean isAddPosition() {
        return addPosition;
    }

    @Override
    public int getPositionCount() {
        return addPosition ? 2 : 0;
    }

    public int getOpponentPositionCount() {
        return 2;
    }

    @Override
    public int getOutputCount() {
        return outputCount;
    }

    @Override
    public boolean isAddBoost() {
        return addBoost;
    }

    @Override
    public int getBoostCount() {
        return addBoost ? 1 : 0;
    }

    @Override
    public int getRememberCount() {
        return rememberCount;
    }

    @Override
    public int getMaxRayLength() {
        return maxRayLength;
    }

}
