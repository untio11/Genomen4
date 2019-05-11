package AI.ConnectFour.Player;

import AI.ConnectFour.PlayConnectFour;

public abstract class ConnectFourPlayer {

    protected PlayConnectFour game;
    protected int player;

    public ConnectFourPlayer() {

    }

    public void setGame(PlayConnectFour game) {
        this.game = game;
    }

    public void setPlayerId(int player) {
        this.player = player;
    }

    public abstract void init();

    public abstract void requestMove(int[][] board);

    public abstract void performMove(int index);
}
