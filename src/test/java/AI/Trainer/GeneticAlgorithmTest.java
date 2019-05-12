package AI.Trainer;

import AI.ConnectFour.ConnectFourTrainer;
import AI.ConnectFour.PlayConnectFour;
import AI.ConnectFour.Player.AIConnectFourPlayer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.util.LinkedHashMap;
import java.util.List;

public class GeneticAlgorithmTest {

    static PlayConnectFour game;

    @BeforeClass
    public static void createGame() {
        game = new PlayConnectFour(true);
    }

    @Test
    public void performPlayerEvolution() {
        LinkedHashMap<AIConnectFourPlayer, Integer> sortedPlayers = new LinkedHashMap<>();
        AIConnectFourPlayer p1 = new AIConnectFourPlayer();
        AIConnectFourPlayer p2 = new AIConnectFourPlayer();
        p1.setGame(game);
        p2.setGame(game);
        p1.setPlayerId(1);
        p2.setPlayerId(2);
        p1.init();
        p2.init();

        sortedPlayers.put(p1, 1);
        sortedPlayers.put(p2, 1);

        GeneticAlgorithm<AIConnectFourPlayer> geneticAlgorithm = new GeneticAlgorithm<>(new ConnectFourTrainer(2, 1));

        List<AIConnectFourPlayer> newPlayers = geneticAlgorithm.performPlayerEvolution(sortedPlayers);
    }

    @Test
    public void accumulatePlayerScores() {
        LinkedHashMap<AIConnectFourPlayer, Integer> sortedPlayers = new LinkedHashMap<>();
        LinkedHashMap<AIConnectFourPlayer, Integer> expectedAccumulatedPlayers = new LinkedHashMap<>();

        AIConnectFourPlayer p1 = new AIConnectFourPlayer();
        AIConnectFourPlayer p2 = new AIConnectFourPlayer();
        AIConnectFourPlayer p3 = new AIConnectFourPlayer();
        AIConnectFourPlayer p4 = new AIConnectFourPlayer();
        AIConnectFourPlayer p5 = new AIConnectFourPlayer();

        sortedPlayers.put(p1, 5);
        sortedPlayers.put(p2, 5);
        sortedPlayers.put(p3, 2);
        sortedPlayers.put(p4, 1);
        sortedPlayers.put(p5, 0);

        expectedAccumulatedPlayers.put(p5, 0);
        expectedAccumulatedPlayers.put(p4, 1);
        expectedAccumulatedPlayers.put(p3, 3);
        expectedAccumulatedPlayers.put(p2, 8);
        expectedAccumulatedPlayers.put(p1, 13);

        GeneticAlgorithm<AIConnectFourPlayer> geneticAlgorithm = new GeneticAlgorithm<>(new ConnectFourTrainer(2, 1));

        LinkedHashMap<AIConnectFourPlayer, Integer> accumulatedPlayers = geneticAlgorithm.accumulatePlayerScores(sortedPlayers);

        Assert.assertEquals(expectedAccumulatedPlayers.size(), accumulatedPlayers.size());

        for (AIConnectFourPlayer s : accumulatedPlayers.keySet()) {
            Assert.assertEquals(expectedAccumulatedPlayers.get(s), accumulatedPlayers.get(s));
        }

    }

    @Test
    public void testINDArrayMask() {
        INDArray zeros = Nd4j.rand(5, 5); //Nd4j.zeros(5, 5);
        INDArray ones = Nd4j.rand(5, 5); //Nd4j.ones(5, 5);


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
}