package GameState.Entities;

import org.joml.Vector3f;

/**
 * Class representing a light source.
 */
public class LightSource extends Entity {
    private float range;
    private Vector3f colour;

    /**
     * Initialize a lightsource with the appropriate properties
     *
     * @param position The position of the light source
     * @param colour   The colour of the light (r,g,b) with r, g, b in [0,1]
     * @param range    The range in distance units (whatever those will be later on)
     */
    LightSource(Vector3f position, Vector3f colour, float range) {
        super(position);
        this.colour = colour;
        this.range = range;
    }

    Vector3f getColour() {
        return this.colour;
    }

    float getIntensity() {
        return range;
    }
}
