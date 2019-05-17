package AI.ConnectFour;

import AI.ConnectFour.Player.AIConnectFourPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;

public class ConnectFourTrainerTest {

    @Test
    public void createPlayer() {
        int expectedPlayers = 123;
        ConnectFourTrainer trainer = new ConnectFourTrainer(expectedPlayers, 1);

        trainer.init();
        Assert.assertEquals(expectedPlayers, trainer.getPlayers().size());
    }

    @Test
    public void createCompetition() {
        int players = 100;
        int expectedCompetitions = players * players - players;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players, 1);

        trainer.init();
        Assert.assertEquals(expectedCompetitions, trainer.getCompetition().size());
    }

    @Test
    public void playCompetition() {
        int players = 5;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players, 1);

        trainer.init();
        trainer.runGeneticAlgorithm();

    }

    @Test
    public void evaluatePlayers() {
        int players = 5;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players, 1);

        trainer.init();
        trainer.runGeneticAlgorithm();
        LinkedHashMap<AIConnectFourPlayer, Integer> sortedPlayers = trainer.evaluatePlayers();
    }

    @Test
    public void runGeneticAlgorithm() {
        int players = 10;
        int iterations = 2;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players, iterations);

        trainer.init();
        trainer.runGeneticAlgorithm();
    }
}