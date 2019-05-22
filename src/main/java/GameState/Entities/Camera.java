package GameState.Entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The class that keeps track of the camera position in the world
 */
public class Camera extends Entity {
    private Actor actor;

    public Camera(Vector3f position) {
        super(position);
        this.rotation.x = 90f; // Rotate the camera down(?)
    }

    public Camera(Actor actor) {
        this.actor = actor;
        updatePosition();
        this.rotation.x = 90f; // Rotate the camera down(?)
    }

    public Camera() {
        super();
    }

    public void updatePosition() {
        setPosition(new Vector3f(actor.getPosition().x, actor.getPosition().y + 50, actor.getPosition().z));
    }

    public float getPitch() {
        return this.rotation.x;
    }

    public float getYaw() {
        return this.rotation.z;
    }

    public float getRoll() {
        return this.rotation.y;
    }
}
