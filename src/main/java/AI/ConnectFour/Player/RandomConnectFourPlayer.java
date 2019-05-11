package AI.ConnectFour.Player;

import AI.ConnectFour.PlayConnectFour;

import java.util.Random;

public class RandomConnectFourPlayer extends ConnectFourPlayer {

    public RandomConnectFourPlayer() {
        super();
    }

    @Override
    public void init() {

    }

    @Override
    public void requestMove(int[][] board) {
        // Figure out the best move

        Random r = new Random();

        int x = r.nextInt(game.boardSize());
        int y = r.nextInt(game.boardSize());

        while (!game.checkMove(x, y)) {
            x = r.nextInt(game.boardSize());
            y = r.nextInt(game.boardSize());
        }

         game.doMove(x, y, player);
    }

    @Override
    public void performMove(int index) {

    }
}
