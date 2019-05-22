package GameState.Entities;

import GameState.Position;
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
        int tileY = (int) (position.z - size / 2);                  //the tile where the upper side of the actor is
        float offY = (position.y - size / 2) - tileY;               //the y offset in that tile
        int tileXLeft = (int) (position.x - size / 2);              //the tile where the left side of the actor is in
        int tileXRight = (int) (position.x + size / 2 - 0.0001);    //the tile where the right side of the actor is in
        double distance = dt * speed;
        if (world.getCollision((int) position.x, tileY - 1)     //if upper tile is an obstacle
                || world.getCollision(tileXRight, tileY - 1)
                || world.getCollision(tileXLeft, tileY - 1)) {
            if (distance > offY) {  //in case that the travelled distance would lead into an obstacle
                position.z -= offY;
                return;
            }
        }
        position.z -= distance;
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
        position.z += distance;
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

    public Vector3f get3DPosition() {
        return new Vector3f(position.x, position.z, position.y);
    }
}
