package Graphics.RenderEngine;

public interface RenderInterface {
    /**
     * Prepare the renderer for use. Nice to bind the buffers.
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

    void renderMenu();
}
