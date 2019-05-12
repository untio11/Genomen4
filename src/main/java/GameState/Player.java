package GameState;

public class Player {

    private float speed;
    private int tileX, tileY;
    private float offX, offY;
    private float posX, posY;
    private int width, height;
    private boolean kidnapper;
    private World world;

    public Player(int posX, int posY, boolean kidnapper, World world) {
        this.world = world;
        this.kidnapper = kidnapper;
        speed = kidnapper ? 90 : 100;

        this.tileX = posX;
        this.tileY = posY;
        this.offX = 0;
        this.offY = 0;
        this.posX = posX * World.TS;
        this.posY = posY * World.TS;
        //todo allow for different size
        this.width = World.TS;
        this.height = World.TS;
    }

    public void moveLeft(double dt) {
        if (world.getCollision(tileX - 1, tileY) || world.getCollision(tileX - 1, tileY + (int) Math.signum((int) offY))) {
            if (offX > 0) {
                offX -= dt * speed;
                if (offX < 0) {
                    offX = 0;
                }
            } else {
                offX = 0;
            }
        } else {
            offX -= dt * speed;
        }
        while (offX < -World.TS / 2) {
            tileX--;
            offX += World.TS;
        }
        posX = tileX * World.TS + offX;
    }

    public void moveRight(double dt) {
        if (world.getCollision(tileX + 1, tileY) || world.getCollision(tileX + 1, tileY + (int) Math.signum((int) offY))) {
            if (offX < 0) {
                offX += dt * speed;
                if (offX > 0) {
                    offX = 0;
                }
            } else {
                offX = 0;
            }
        } else {
            offX += dt * speed;
        }
        while (offX > World.TS / 2) {
            tileX++;
            offX -= World.TS;
        }
        posX = tileX * World.TS + offX;
    }

    public void moveUp(double dt) {
        if (world.getCollision(tileX, tileY - 1) || world.getCollision(tileX + (int) Math.signum((int) offX), tileY - 1)) {
            if (offY > 0) {
                offY -= dt * speed;
                if (offY < 0) {
                    offY = 0;
                }
            } else {
                offY = 0;
            }
        } else {
            offY -= dt * speed;
        }
        while (offY < -World.TS / 2) {
            tileY--;
            offY += World.TS;
        }
        posY = tileY * World.TS + offY;
    }

    public void moveDown(double dt) {
        if (world.getCollision(tileX, tileY + 1) || world.getCollision(tileX + (int) Math.signum((int) offX), tileY + 1)) {
            if (offY < 0) {
                offY += dt * speed;
                if (offY > 0) {
                    offY = 0;
                }
            } else {
                offY = 0;
            }
        } else {
            offY += dt * speed;
        }
        while (offY > World.TS / 2) {
            tileY++;
            offY -= World.TS;
        }
        posY = tileY * World.TS + offY;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isKidnapper() {
        return kidnapper;
    }
}
