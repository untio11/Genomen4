package AI.ConnectFour;

import org.junit.Assert;
import org.junit.Test;

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
        int players = 2;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players);

        trainer.init();
        trainer.playCompetition();

    }
}