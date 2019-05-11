package GameState;

public class Player extends Entity {
    private float speed;
    private boolean kidnapper;

    public Player(int posX, int posY, boolean kidnapper, int tileWidth) {
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 80: 100;
        setEntityPosition(tileWidth*posX, tileWidth*posY);
        setTilePosition(posX, posY);
        setOffset(0, 0);
        setSize(16);
    }
}
