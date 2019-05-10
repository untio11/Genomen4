package AI.ConnectFour.Player;

import AI.ConnectFour.PlayConnectFour;

public abstract class ConnectFourPlayer {

    protected PlayConnectFour game;
    protected int player;

    public ConnectFourPlayer(PlayConnectFour game, int player) {
        this.game = game;
        this.player = player;
    }

    public abstract void requestMove(int[][] board);

    public abstract void performMove(int index);
}
