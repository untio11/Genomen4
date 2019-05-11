package AI.ConnectFour;

import AI.ConnectFour.Player.AIConnectFourPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;

public class ConnectFourTrainerTest {

    @Test
    public void createPlayer() {
        int expectedPlayers = 123;
        ConnectFourTrainer trainer = new ConnectFourTrainer(expectedPlayers);

        trainer.init();
        Assert.assertEquals(expectedPlayers, trainer.getPlayers().size());
    }

    @Test
    public void createCompetition() {
        int players = 100;
        int expectedCompetitions = players * players - players;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players);

        trainer.init();
        Assert.assertEquals(expectedCompetitions, trainer.getCompetition().size());
    }

    @Test
    public void playCompetition() {
        int players = 5;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players);

        trainer.init();
        trainer.playCompetition();

    }

    @Test
    public void evaluatePlayers() {
        int players = 5;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players);

        trainer.init();
        trainer.playCompetition();
        LinkedHashMap<AIConnectFourPlayer, Integer> sortedPlayers = trainer.evaluatePlayers();
    }
}