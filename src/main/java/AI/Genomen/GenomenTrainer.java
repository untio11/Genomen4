package AI.Genomen;

import AI.ConnectFour.Player.AIConnectFourPlayer;
import AI.Genomen.Player.AIGenomenPlayer;
import AI.Trainer.BiAIGameTrainer;
import GameState.World;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// TODO: Switch out the World class with the Game class that can run the game headless
public class GenomenTrainer extends BiAIGameTrainer<AIGenomenPlayer, AIGenomenPlayer, World> {

    public GenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return "genomen";
    }

    @Override
    protected AIGenomenPlayer createPlayer1() {
        return new AIGenomenPlayer();
    }

    @Override
    protected AIGenomenPlayer createPlayer1(Map<String, INDArray> paramTable) {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected AIGenomenPlayer createPlayer2() {
        return new AIGenomenPlayer();
    }

    @Override
    protected AIGenomenPlayer createPlayer2(Map<String, INDArray> paramTable) {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected List<Pair<AIGenomenPlayer, AIGenomenPlayer>> createCompetition(List<AIGenomenPlayer> players1, List<AIGenomenPlayer> players2) {
        List<Pair<AIGenomenPlayer, AIGenomenPlayer>> competition = new ArrayList<>();

        // Create a bipartite graph as the competition
        for (AIGenomenPlayer player1 : players1) {
            for (AIGenomenPlayer player2 : players2) {
                competition.add(new Pair<>(player1, player2));
            }
        }

        return competition;
    }

    @Override
    protected World createGame(Pair<AIGenomenPlayer, AIGenomenPlayer> players) {
        // TODO: Over here we should be able to initialize a game that is headless and assign the players to it
        // TODO: We should also be able to add listeners to the game end state
        return null;
    }

    @Override
    protected void playGame(World game) {
        // TODO: Start the Genomen game headless and play it
        // TODO: Retrieve the results and store them for the players
    }
}
