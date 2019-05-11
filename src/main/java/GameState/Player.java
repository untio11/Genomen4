package GameState;

public class Player extends Entity {
    private float speed;
    private boolean kidnapper;

    public Player(int posX, int posY, boolean wasd, boolean kidnapper) {
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 80: 100;
        setPosition(16*posX, 16*posY);
        setTilePosition(posX, posY);
        setOffset(0, 0);
        setSize(16);
    }
}
