package GameState.Entities;

import org.joml.Vector3f;

/**
 * The class that keeps track of the camera position in the world
 */
public class Camera extends Entity {
    public Camera(Vector3f position) {
        super(position);
        this.rotation.x = 90f;
    }

    public Camera() {
        super();
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
