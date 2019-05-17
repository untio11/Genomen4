package AI.Genomen;

import AI.Genomen.Player.AIGenomenPlayer;
import AI.Trainer.BiAIGameTrainer;
import GameState.World;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.List;
import java.util.Map;

// TODO: Switch out the World class with the Game class that can run the game headless
public class GenomenTrainer extends BiAIGameTrainer<AIGenomenPlayer, AIGenomenPlayer, World> {

    public GenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return null;
    }

    @Override
    protected void playGame(World game) {

    }

    @Override
    protected AIGenomenPlayer createPlayer1() {
        return null;
    }

    @Override
    protected AIGenomenPlayer createPlayer1(Map<String, INDArray> paramTable) {
        return null;
    }

    @Override
    protected AIGenomenPlayer createPlayer2() {
        return null;
    }

    @Override
    protected AIGenomenPlayer createPlayer2(Map<String, INDArray> paramTable) {
        return null;
    }

    @Override
    protected List<Pair<AIGenomenPlayer, AIGenomenPlayer>> createCompetition(List<AIGenomenPlayer> players1, List<AIGenomenPlayer> players2) {
        return null;
    }

    @Override
    protected World createGame(Pair<AIGenomenPlayer, AIGenomenPlayer> players) {
        return null;
    }
}
