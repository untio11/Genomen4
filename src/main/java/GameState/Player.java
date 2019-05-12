package GameState;

public class Player extends Entity {
    private float speed;
    private boolean kidnapper;
    World world;
    int tileWidth;

    public Player(int posX, int posY, boolean kidnapper, World world) {
        this.tileWidth = (int) world.getTileWidth();
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 80: 100;
        setEntityPosition(tileWidth*posX, tileWidth*posY);
        setTilePosition(posX, posY);
        setOffset(0, 0);
        setSize(tileWidth);
        this.world = world;
    }

    public void moveLeft(float dt) {
        float offX = getOffset().getX();
        float offY = getOffset().getY();

        if (gm.getCollision(tileX - 1,tileY) || gm.getCollision(tileX - 1,tileY + (int) Math.signum((int) offY))) {
            if (offX > 0) {
                offX -= dt*speed;
                if (offX <0) {
                    offX = 0;
                }
            } else {
                offX = 0;
            }
        } else {
            offX -= dt*speed;
        }
        if (offX < -16 /2) {
            tileX--;
            offX += 16;
        }
    }
}
