package Graphics.RenderEngine;

/**
 * General I/O that a renderer should have in order to abstract away from the actual implementation
 */
public interface AbstractRenderer {
    /**
     * Prepare the renderer for use. Nice to bind the buffers.
     */
    void init(Scene scene);

    /**
     * Render the given scene on screen.
     */
    void render(Scene scene);

    /**
     * Clean all the memory or something.
     */
    void clean();
}