package AI.ConnectFour;

import AI.AIGameTrainer;
import AI.ConnectFour.Player.AIConnectFourPlayer;
import util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ConnectFourTrainer extends AIGameTrainer<AIConnectFourPlayer> {

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
}
