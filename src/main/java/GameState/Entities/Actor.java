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
    private int tileX, tileY;
    private float offX, offY;
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
        // TODO: Something with offsets and models being centered in the tile.
        this.offX = 0;
        this.offY = 0;
        this.tileX = (int) position.x;
        this.tileY = (int) position.y;
        this.kidnapper = kidnapper;
        this.speed = kidnapper ? 90 : 100;
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

    public void moveUp(double dt) {}

    public void moveDown(double dt) {}

    public void moveLeft(double dt) {}

    public void moveRight(double dt) {}

    public float getSize() {
        return this.size;
    }

    public boolean isKidnapper() {
        return kidnapper;
    }
}
