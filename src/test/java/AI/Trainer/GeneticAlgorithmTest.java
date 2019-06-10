package AI.Trainer;

import AI.ConnectFour.PlayConnectFour;
import AI.Genomen.Player.AIGenomenPlayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;

public class GeneticAlgorithmTest {

    static PlayConnectFour game;

    @BeforeClass
    public static void createGame() {
        game = new PlayConnectFour(true);
    }

    @Test
    public void testINDArrayMask() {
        INDArray zeros = Nd4j.rand(5, 5); //Nd4j.zeros(5, 5);
        INDArray ones = Nd4j.rand(5, 5); //Nd4j.ones(5, 5);

        zeros.subi(0.5);
        zeros = zeros.mul(zeros).mul(zeros);
        zeros.muli(8);

//        zeros.muli(2).subi(1);

        System.out.println(zeros);
        System.out.println(ones);

        INDArray crossover = this.crossover(zeros, ones);
        System.out.println(crossover);
    }

    private INDArray crossover(INDArray a1, INDArray a2) {
        INDArray mask = Nd4j.zeros(a1.shape());
        createRandomMask(mask);
        return a1.putWhereWithMask(mask, a2);
    }

    private INDArray createRandomMask(INDArray mask) {
        INDArray source = Nd4j.create(new float[] {0, 1});
        INDArray probs = Nd4j.create(new float[] {0.5f, 0.5f});
        Nd4j.choice(source, probs, mask);
        return mask;
    }

    @Test
    public void testNetworkLoading() {
        AIGenomenPlayer player = new AIGenomenPlayer();

        File f = new File("res/network/1560138928134-single-genomen-1-8986.net");
        MultiLayerNetwork net;
        try {
            net = player.loadNetwork(f);
            System.out.println(net.paramTable());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}