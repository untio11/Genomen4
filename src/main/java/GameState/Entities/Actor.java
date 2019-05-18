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
     * Initialize a player with the appropriate properties
     *
     * @param model    The model that the player should have: We probably want to change this to some loose reference
     * @param position The position of the player
     * @param rotation The rotation of the model
     * @param scale    The size of the model (I think)
     */
    public Actor(World world, TexturedModel model, float size, Vector3f position, Vector3f rotation, float scale, boolean kidnapper) {
        super(position);
        this.model = model;
        this.rotation = rotation;
        this.scale = scale;
        this.size = size;
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 3 : 4;
        this.world = world;
    }

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

    public void moveUp(double dt) {
        int tileY = (int) (position.y - size / 2);
        float offY = (position.y - size / 2) - tileY;
        int tileXLeft = (int) (position.x - size / 2);
        int tileXRight = (int) (position.x + size / 2);
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY - 1)
                || world.getCollision(tileXRight, tileY - 1)
                || world.getCollision(tileXLeft, tileY - 1)) {
            if (distance > offY) {
                position.y -= offY;
                return;
            }
        }
        position.y -= distance;
        //todo: add rotation
    }

    public void moveDown(double dt) {
        int tileY = (int) (position.y + size / 2 - 0.00001);
        float offY = tileY + 1 - (position.y + size / 2);
        int tileXLeft = (int) (position.x - size / 2);
        int tileXRight = (int) (position.x + size / 2);
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

    public void moveLeft(double dt) {
        int tileX = (int) (position.x - size / 2);
        float offX = (position.x - size / 2) - tileX;
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2);
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

    public void moveRight(double dt) {
        int tileX = (int) (position.x + size / 2 - 0.00001);
        float offX = tileX + 1 - (position.x + size / 2);
        int tileYUp = (int) (position.y - size / 2);
        int tileYDown = (int) (position.y + size / 2);
        double distance = dt * speed;
        if (world.getCollision(tileX + 1, (int) position.y) || world.getCollision(tileX + 1, tileYUp) || world.getCollision(tileX + 1, tileYDown)) {
            if (distance > offX) {
                position.x += offX;
                return;
            }
        }
        position.x += distance;
        //todo: add rotation
    }

    public float getSize() {
        return this.size;
    }

    public boolean isKidnapper() {
        return kidnapper;
    }
}
