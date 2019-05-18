package GameState.Entities;

import com.sun.istack.internal.NotNull;
import org.joml.Vector3f;

/**
 * General entity that has a 3D position and rotation somewhere in the world.
 */
public abstract class Entity {
    static private long ID_counter = 0;
    protected long ID;
    protected Vector3f position;
    protected Vector3f rotation = new Vector3f(0f, 0f, 0f);

    /**
     * Create a new entity at the given position
     *
     * @param position The coordinates (x,y,z) defining the position of the entity
     */
    Entity(Vector3f position) {
        this.position = position;
        ID = ID_counter++;
    }

    Entity() {
        this.position = new Vector3f(0f, 0f, 0f);
        ID = ID_counter++;
    }

    /**
     * Move the entity in the given direction from the current position
     *
     * @param dx How much the entity should move in the x-dimension
     * @param dy How much the entity should move in the y-dimension
     * @param dz How much the entity should move in the z-dimension
     */
    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    /**
     * Adjust the rotational angles of the entity.
     *
     * @param dx The amount of degrees to rotate the entity around the x axis (pitch)
     * @param dy The amount of degrees to rotate the entity around the y axis (roll)
     * @param dz The amount of degrees to rotate the entity around the z axis (yaw)
     */
    public void increaseRotation(float dx, float dy, float dz) {
        this.rotation.x += dx;
        this.rotation.y += dy;
        this.rotation.z += dz;
    }

    /**
     * Current position of the entity
     *
     * @return (x, y, z) as the current position of the entity.
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Instantaneously change the position of the entity (teleportation upgrade when?)
     *
     * @param position New position of the entity
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Gives a short textual description of the entity
     *
     * @return "[ID]: position(x, y, z), rotation(pitch, roll, yaw)"
     */
    @NotNull
    public String toString() {
        return String.format("[%s]: position(%f, %f, %f), rotation(%f, %f, %f)",
                ID, position.x, position.y, position.z, rotation.x, rotation.y, rotation.z);
    }
}
