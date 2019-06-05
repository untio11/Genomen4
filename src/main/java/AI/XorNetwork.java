package AI;


import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.distribution.UniformDistribution;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Sgd;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import java.util.Map;

/**
 * Example taken from https://github.com/deeplearning4j/dl4j-examples/blob/master/dl4j-examples/src/main/java/org/deeplearning4j/examples/feedforward/xor/XorExample.java
 */
public class XorNetwork {

    protected MultiLayerNetwork net;
    private static final int seed = 1234;
    private static final int nEpochs = 10000;

    protected DataSet dataset;

    protected INDArray input;
    protected INDArray labels;

    public XorNetwork() {
        this.createData();

        this.createNetwork();

        net.setListeners(new ScoreIterationListener(100));

        System.out.println(net.summary());

        // here the actual learning takes place
        for( int i=0; i < nEpochs; i++ ) {
            net.fit(dataset);
        }

        INDArray output = net.output(dataset.getFeatures());
        System.out.println(output);

        Evaluation eval = new Evaluation();
        eval.eval(dataset.getLabels(), output);
        System.out.println(eval.stats());

        // The learning of the network is done, now create a new network that is a copy of the original to test the param tables

        System.out.println("=================");

        Map<String, INDArray> paramTable = net.paramTable();

        this.createNetwork();

        this.net.setParamTable(paramTable);

        INDArray output2 = net.output(dataset.getFeatures());
        System.out.println(output2);

        Evaluation eval2 = new Evaluation();
        eval2.eval(dataset.getLabels(), output2);
        System.out.println(eval2.stats());

    }

    protected void createData() {
        // list off input values, 4 training samples with data for 2
        // input-neurons each
        input = Nd4j.zeros(4, 2);

        // correspondending list with expected output values, 4 training samples
        // with data for 2 output-neurons each
        labels = Nd4j.zeros(4, 2);

        // create first dataset
        // when first input=0 and second input=0
        input.putScalar(new int[]{0, 0}, 0);
        input.putScalar(new int[]{0, 1}, 0);
        // then the first output fires for false, and the second is 0 (see class comment)
        labels.putScalar(new int[]{0, 0}, 1);
        labels.putScalar(new int[]{0, 1}, 0);

        // when first input=1 and second input=0
        input.putScalar(new int[]{1, 0}, 1);
        input.putScalar(new int[]{1, 1}, 0);
        // then xor is true, therefore the second output neuron fires
        labels.putScalar(new int[]{1, 0}, 0);
        labels.putScalar(new int[]{1, 1}, 1);

        // same as above
        input.putScalar(new int[]{2, 0}, 0);
        input.putScalar(new int[]{2, 1}, 1);
        labels.putScalar(new int[]{2, 0}, 0);
        labels.putScalar(new int[]{2, 1}, 1);

        // when both inputs fire, xor is false again - the first output should fire
        input.putScalar(new int[]{3, 0}, 1);
        input.putScalar(new int[]{3, 1}, 1);
        labels.putScalar(new int[]{3, 0}, 1);
        labels.putScalar(new int[]{3, 1}, 0);

        // create dataset object
        dataset = new DataSet(input, labels);
    }

    protected void createNetwork() {
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .updater(new Sgd(0.1))
                .seed(seed)
                .biasInit(0) // init the bias with 0 - empirical value, too
                // from "http://deeplearning4j.org/architecture": The networks can
                // process the input more quickly and more accurately by ingesting
                // minibatches 5-10 elements at a time in parallel.
                // this example runs better without, because the dataset is smaller than
                // the mini batch size
                .miniBatch(false)
                .list()
                .layer(new DenseLayer.Builder()
                        .nIn(2)
                        .nOut(4)
                        .activation(Activation.SIGMOID)
                        // random initialize weights with values between 0 and 1
//                        .weightInit(WeightInit.DISTRIBUTION)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .nOut(2)
                        .activation(Activation.SOFTMAX)
//                        .weightInit(WeightInit.DISTRIBUTION)
                        .weightInit(new UniformDistribution(0, 1))
                        .build())
                .build();

        net = new MultiLayerNetwork(conf);
        net.init();
    }
}
