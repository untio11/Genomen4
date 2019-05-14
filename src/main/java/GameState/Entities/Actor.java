package GameState.Entities;

import Graphics.Models.TexturedModel;
import org.joml.Vector3f;

/**
 * For keeping track of the players
 */
public class Actor extends Entity {

    private TexturedModel model;
    private float scale;

    /**
     * Initialize a player with the appropriate properties
     *
     * @param model    The model that the player should have: We probably want to change this to some loose reference
     * @param position The position of the player
     * @param rotX     The rotation around the x-axis
     * @param rotY     The rotation around the y-axis
     * @param rotZ     The rotation around the z-axis
     * @param scale    The size of the model (I think)
     */
    public Actor(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(position);
        this.model = model;
        this.rotation.x = rotX;
        this.rotation.y = rotY;
        this.rotation.z = rotZ;
        this.scale = scale;
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
}
