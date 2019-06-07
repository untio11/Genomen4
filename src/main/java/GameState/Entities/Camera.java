package GameState.Entities;

import org.joml.Vector2f;
import org.joml.Vector3f;
import util.Observable;
import util.Observer;

/**
 * The class that keeps track of the camera position in the world
 */
public class Camera extends Entity implements Observer<Actor> {
    public Camera(Vector3f position) {
        super(position);
        this.rotation.x = 90f; // Rotate the camera down(?)
    }

    public Camera() {
        this.rotation.x = 90f; // Rotate the camera down(?)
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

    @Override
    public void update(Actor observable) {
        Vector3f actor_pos = observable.get3DPosition();
        this.position.x = actor_pos.x;
        // Height should be constant
        this.position.y = 3f;
        this.position.z = actor_pos.z; // Though it's actually the y-axis of the player
    }
}
