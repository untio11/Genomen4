package GameState;

import org.joml.Vector3f;

public class Camera {

    private Vector3f position = new Vector3f(0,0,0);
    private float pitch;
    private float yaw;
    private float roll;

    public void moveUp() {
        position.y += 0.05f;
    }

    public void moveDown() {
        position.y -= 0.05f;
    }

    public void moveLeft() {
        position.x -= 0.05f;
    }

    public void moveRight() {
        position.x += 0.05f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }
}
