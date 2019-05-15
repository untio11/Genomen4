package AI.Genomen;

import AI.Genomen.Player.AIGenomenPlayer;
import AI.Trainer.AIGameTrainer;
import GameState.World;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.List;
import java.util.Map;

// TODO: Switch out the World class with the Game class that can run the game headless
public class GenomenTrainer extends AIGameTrainer<AIGenomenPlayer, World> {

    public GenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected AIGenomenPlayer createPlayer() {
        return null;
    }

    @Override
    protected AIGenomenPlayer createPlayer(Map<String, INDArray> paramTable) {
        return null;
    }

    @Override
    protected List<Pair<AIGenomenPlayer, AIGenomenPlayer>> createCompetition(List<AIGenomenPlayer> players) {
        return null;
    }

    @Override
    protected World createGame(Pair<AIGenomenPlayer, AIGenomenPlayer> players) {
        return null;
    }

    @Override
    protected void playGame(World game) {

    }
}
