package GameState.Entities;

import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * The class that keeps track of the camera position in the world
 */
public class Camera extends Entity {
    public Camera(Vector3f position) {
        super(position);
        this.rotation.x = 90f; // Rotate the camera down(?)
    }

    public Camera(float x, float y, float z) {
        setPosition(new Vector3f(x, y, z));
        this.rotation.x = 90f; // Rotate the camera down(?)
    }

    public Camera() {
        super();
    }

    /**
     * Turn the camera so it is looking at the target
     * @param target The location of the target.
     */
    public void lookAt(Vector3f target) {

    }

    public void moveUp() {
        position.z -= 0.05f;
    }

    public void moveDown() {
        position.z += 0.05f;
    }

    public void moveLeft() {
        position.x -= 0.05f;
    }

    public void moveRight() {
        position.x += 0.05f;
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
