package GameState.Entities;

import GameState.World;
import Graphics.Models.TexturedModel;
import org.joml.Vector3f;

/**
 * For keeping track of the players
 */
public class Actor extends Entity {
    private TexturedModel model;
    private float scale;
    private float speed;
    private float size;
    private boolean kidnapper;
    private World world;

    /**
     * Initialize a actor with the appropriate properties
     *
     * @param model     The model that the actor should have: We probably want to change this to some loose reference
     * @param position  The position of the actor
     * @param rotation  The rotation of the model
     * @param scale     The size of the model (I think)
     * @param kidnapper The role of the actor
     */
    public Actor(World world, TexturedModel model, float size, Vector3f position, Vector3f rotation, float scale, boolean kidnapper) {
        super(position);
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
        this.size = size;
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 3 : 4; //different speed for the two players
        this.world = world;
    }

    /**
     * Move the actor up, unless obstacle
     * @param dt time elapsed
     */
    public void moveUp(double dt) {
        int tileY = (int) (position.y - size / 2);                  //the tile where the upper side of the actor is
        float offY = (position.y - size / 2) - tileY;               //the y offset in that tile
        int tileXLeft = (int) (position.x - size / 2);              //the tile where the left side of the actor is in
        int tileXRight = (int) (position.x + size / 2 - 0.0001);    //the tile where the right side of the actor is in
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY - 1)     //if upper tile is an obstacle
                || world.getCollision(tileXRight, tileY - 1)
                || world.getCollision(tileXLeft, tileY - 1)) {
            if (distance > offY) {  //in case that the travelled distance would lead into an obstacle
                position.y -= offY;
                return;
            }
        }
        position.y -= distance;
        //todo: add rotation
    }

    /**
     * Move the actor down, unless obstacle
     * @param dt time elapsed
     */
    public void moveDown(double dt) {
        int tileY = (int) (position.y + size / 2 - 0.00001);
        float offY = tileY + 1 - (position.y + size / 2);
        int tileXLeft = (int) (position.x - size / 2);
        int tileXRight = (int) (position.x + size / 2 - 0.00001);
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY + 1)
                || world.getCollision(tileXRight, tileY + 1)
                || world.getCollision(tileXLeft, tileY + 1)) {
            if (distance > offY) {
                position.y += offY;
                return;
            }
        }
        position.y += distance;
        //todo: add rotation
    }

    /**
     * Move the actor left, unless obstacle
     * @param dt time elapsed
     */
    public void moveLeft(double dt) {
        int tileX = (int) (position.x - size / 2);
        float offX = (position.x - size / 2) - tileX;
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2 - 0.00001);
        double distance = dt * speed;
        if (world.getCollision(tileX - 1, (int) position.y)
                || world.getCollision(tileX - 1, tileYUp)
                || world.getCollision(tileX - 1, tileYDown)) {
            if (distance > offX) {
                position.x -= offX;
                return;
            }
        }
        position.x -= distance;
        //todo: add rotation
    }

    /**
     * Move the actor right, unless obstacle
     * @param dt time elapsed
     */
    public void moveRight(double dt) {
        int tileX = (int) (position.x + size / 2 - 0.00001);
        float offX = tileX + 1 - (position.x + size / 2);
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2 - 0.0001);
        double distance = dt * speed;
        if (world.getCollision(tileX + 1, (int) position.y)
                || world.getCollision(tileX + 1, tileYUp)
                || world.getCollision(tileX + 1, tileYDown)) {
            if (distance > offX) {
                position.x += offX;
                return;
            }
        }
        position.x += distance;
        //todo: add rotation
    }

    public float getSize() { return this.size; }

    public boolean isKidnapper() { return kidnapper; }

    public TexturedModel getModel() {
        return model;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
    }

    public float getRotX() {
        return this.rotation.x;
    }

    public void setRotX(float rotX) {
        this.rotation.x = rotX;
    }

    public float getRotY() {
        return this.rotation.y;
    }

    public void setRotY(float rotY) {
        this.rotation.y = rotY;
    }

    public float getRotZ() {
        return this.rotation.z;
    }

    public void setRotZ(float rotZ) {
        this.rotation.z = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
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
            int mapX = (int) position.x;
            int mapY = (int) position.y;
            float posX = position.x;
            float posY = position.y;
            System.out.println("X: " + posX + " Y: " + posY + " mapX: " + mapX + " mapY: " + mapY);
            int hit = 0;

            //calculate step and initial sideDist
            if (rayDirX < 0) {
                stepX = -1;
                sideDistX = (posX - mapX) * deltaDistX;
            } else {
                stepX = 1;
                sideDistX = (mapX + 1 - posX) * deltaDistX;
            } if (rayDirY < 0) {
                stepY = -1;
                sideDistY = (posY - mapY) * deltaDistY;
            } else {
                stepY = 1;
                sideDistY = (mapY + 1 - posY) * deltaDistY;
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
