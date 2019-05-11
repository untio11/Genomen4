package AI.ConnectFour;

import AI.AIGameTrainer;
import AI.ConnectFour.Player.AIConnectFourPlayer;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConnectFourTrainer extends AIGameTrainer<AIConnectFourPlayer, PlayConnectFour> {

    public ConnectFourTrainer(int nPlayers) {
        super(nPlayers);
    }

    public static void main(String[] args) {
        ConnectFourTrainer trainer = new ConnectFourTrainer(100);
        trainer.init();
    }

    @Override
    protected AIConnectFourPlayer createPlayer() {
        return new AIConnectFourPlayer();
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
        System.out.println("Game start");

        game.renderGUI();
        game.start();

        System.out.println("Game done");
    }
}
