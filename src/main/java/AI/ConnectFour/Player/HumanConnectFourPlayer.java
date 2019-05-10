package AI.ConnectFour.Player;

import AI.ConnectFour.PlayConnectFour;

public class HumanConnectFourPlayer extends ConnectFourPlayer {

    public HumanConnectFourPlayer(PlayConnectFour game, int player) {
        super(game, player);
    }

    @Override
    public void requestMove(int[][] board) {
        // Figure out the best move

        // game.requestMove(x, y, player);
    }

    @Override
    public void performMove(int index) {
        int x = index % game.boardSize();
        int y = index / game.boardSize();
        game.doMove(x, y, player);
    }
}
