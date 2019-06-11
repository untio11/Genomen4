package GameState.Entities;

import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import util.Observer;

/**
 * Class representing a light source.
 */
public class LightSource extends Entity implements Observer<Actor> {
    private Vector3f offset;
    private float target_angle;

    public LightSource() {
        this.offset = new Vector3f(0.0f, 0.0f, -0.5f);
    }

    @Override
    public void update(Actor observable) {
        Vector3f actor_pos = observable.get3DPosition();
        actor_pos.add(0, 1.5f, 0, this.position);
    }
}
