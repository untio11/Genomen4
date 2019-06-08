package Graphics.RenderEngine;

/**
 * General I/O that a renderer should have in order to abstract away from the actual implementation
 */
public interface AbstractRenderer {
    /**
     * Prepare the renderer for use. This could just be done in the constructor of the implementations I guess?
     */
    void init(Scene scene);

    /**
     * Render the given scene on screen.
     */
    void render(Scene scene, boolean screamActive, int oppoAngle);

    /**
     * Clean all the memory or something.
     */
    void clean();
}
