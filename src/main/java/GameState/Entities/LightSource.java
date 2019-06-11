package GameState.Entities;

import org.joml.Vector3f;
import util.Observer;

/**
 * Class representing a light source.
 */
public class LightSource extends Entity implements Observer<Actor> {
    private Vector3f offset;

    public LightSource() {
        this.offset = new Vector3f(0.0f, 1.5f, -0.5f);
    }

    @Override
    public void update(Actor observable) {
        Vector3f actor_pos = observable.get3DPosition();
        this.offset.rotateY(observable.getRotY());
        this.position = actor_pos.add(offset, new Vector3f());
    }
}
