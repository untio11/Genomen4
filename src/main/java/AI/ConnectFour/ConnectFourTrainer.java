package AI.ConnectFour;

import AI.Trainer.AIGameTrainer;
import AI.ConnectFour.Player.AIConnectFourPlayer;
import AI.ConnectFour.Player.ConnectFourPlayer;
import AI.ConnectFour.Player.HumanConnectFourPlayer;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConnectFourTrainer extends AIGameTrainer<AIConnectFourPlayer, PlayConnectFour> {

    public ConnectFourTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return "connect-four";
    }

    public static void main(String[] args) {
        int players = 50;
        ConnectFourTrainer trainer = new ConnectFourTrainer(players, 30);

        trainer.init();
        trainer.runGeneticAlgorithm();
        LinkedHashMap<AIConnectFourPlayer, Integer> sortedPlayers = trainer.evaluatePlayers();

        // Play against the best player
        final PlayConnectFour game = new PlayConnectFour(false);
        ConnectFourPlayer human = new HumanConnectFourPlayer();
        game.setPlayer1(human);
        game.setPlayer2(sortedPlayers.entrySet().iterator().next().getKey());

        SwingUtilities.invokeLater(() -> {
            game.renderGUI();
            game.start();
        });
    }

    @Override
    protected AIConnectFourPlayer createPlayer() {
        return new AIConnectFourPlayer();
    }

    @Override
    protected AIConnectFourPlayer createPlayer(Map<String, INDArray> paramTable) {
        AIConnectFourPlayer player = new AIConnectFourPlayer();
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected List<Pair<AIConnectFourPlayer, AIConnectFourPlayer>> createCompetition(List<AIConnectFourPlayer> players) {
        List<Pair<AIConnectFourPlayer, AIConnectFourPlayer>> competition = new ArrayList<>();

        for (AIConnectFourPlayer player1 : players) {
            for (AIConnectFourPlayer player2 : players) {
                if (player1.equals(player2)) {
                    continue;
                }
                competition.add(new Pair<>(player1, player2));
            }
        }

        return competition;
    }

    @Override
    protected PlayConnectFour createGame(Pair<AIConnectFourPlayer, AIConnectFourPlayer> players) {
        PlayConnectFour game = new PlayConnectFour(true);
        game.setPlayer1(players.getFirst());
        game.setPlayer2(players.getSecond());

        game.addEndGameListener(winningPlayer -> {
            System.out.println("Winning player for game is: " + winningPlayer);
        });

        return game;
    }

    @Override
    protected void playGame(PlayConnectFour game) {
        game.renderGUI();
        game.start();

        AIConnectFourPlayer winningPlayer = (AIConnectFourPlayer) game.getWinner();

        if (winningPlayer != null) {
            // The game is not a draw, since there is a winning player
            int score = 1;

            this.setResults(winningPlayer, score);
        }
    }
}
