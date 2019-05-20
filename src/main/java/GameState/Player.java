package GameState;

import org.lwjgl.system.MathUtil;

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

    public void castRays(int num) {
        for (int a = 0; a < num; ++a) {
            int angle = getRayAngle(num, a);
            float rayDirX = (float) Math.cos(Math.toRadians(angle));
            float rayDirY = (float) Math.sin(Math.toRadians(angle));
            System.out.println("RayDirX: " + rayDirX + " RayDirY: " + rayDirY);
            float sideDistX;
            float sideDistY;
            float deltaDistX = Math.abs(1 / rayDirX);
            float deltaDistY = Math.abs(1 / rayDirY);
            int stepX;
            int stepY;
            int mapX = (int) posX / World.TS;
            int mapY = (int) posY / World.TS;
            float posiX = posX / World.TS;
            float posiY = posY / World.TS;
            System.out.println("X: " + posX + " Y: " + posY + " mapX: " + mapX + " mapY: " + mapY);
            int hit = 0;

            //calculate step and initial sideDist
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (posiX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1 - posiX) * deltaDistX;
            } if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (posiY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1 - posiY) * deltaDistY;
            }

            float distance = 0;
            while (hit == 0) {
                //jump to next map square, OR in x-direction, OR in y-direction
                if (sideDistX < sideDistY) {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                } else {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                }
                //Check if ray has hit a wall
                if (!world.getTile(mapX, mapY).isAccessible()) {
                    hit = 1;
                    System.out.println("Hit: " + world.getTileType(mapX, mapY).toString()
                    + " Distance: " + Math.sqrt((Float.isInfinite(sideDistX) ? 0 : Math.pow(sideDistX - deltaDistX, 2)) + (Float.isInfinite(sideDistY) ? 0 : Math.pow(sideDistY - deltaDistY, 2))) + " mapX: " + mapX + " mapY: " + mapY);
                }
            }
        }
    }

    private int getRayAngle(int total, int x) {
        return (x * (360 / total));
    }

    private int getRayQuadrant(int angle) {
        angle %= 360;
        if (angle < 0) {
            angle += 360;
        }
        return (angle / 90) % 4 + 1;
    }
}
