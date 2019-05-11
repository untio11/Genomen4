package AI;

import org.junit.Assert;
import org.junit.Test;
import util.Pair;

import java.util.List;

public class AIGameTrainerTest {

    @Test
    public void createPlayers() {
        int expectedPlayers = 100;
        AIGameTrainer trainer = new AIGameTrainer<Integer, Integer>(expectedPlayers) {
            @Override
            protected Integer createPlayer() {
                return 1;
            }

            @Override
            protected List<Pair<Integer, Integer>> createCompetition(List<Integer> players) {
                return null;
            }

            @Override
            protected Integer createGame(Pair<Integer, Integer> players) {
                return null;
            }

            @Override
            protected void playGame(Integer game) {

            }
        };

        trainer.init();
        Assert.assertEquals(expectedPlayers, trainer.getPlayers().size());
    }
}