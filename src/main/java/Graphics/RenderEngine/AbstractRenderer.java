package Graphics.RenderEngine;

import Graphics.Gui.GuiRenderer;
import Graphics.Gui.MenuRenderer;

/**
 * General I/O that a renderer should have in order to abstract away from the actual implementation
 */
public abstract class AbstractRenderer implements RenderInterface {
    protected static GuiRenderer guiRenderer;
    protected static MenuRenderer menuRenderer, lostRenderer, winRenderer;

    public AbstractRenderer() {
        guiRenderer = new GuiRenderer();
        menuRenderer = new MenuRenderer();
        winRenderer = new MenuRenderer();
        lostRenderer = new MenuRenderer();
    }

    /**
     * Prepare the renderer for use. Nice to bind the buffers.
     */
    public void init(Scene scene) {
        guiRenderer.init(scene);
        menuRenderer.init(scene,0);
        winRenderer.init(scene,1);
        lostRenderer.init(scene,2);
    }

    /**
     * Render the given scene on screen.
     */
    abstract public void render(Scene scene, boolean screamActive, int oppoAngle);

    /**
     * Clean all the memory or something.
     */
    abstract public void clean();

    abstract protected void prepare();

    public void renderMenu() {
        prepare();
        menuRenderer.render();
    }

    public void renderEnd(boolean win) {
        prepare();
        if (win) {
            winRenderer.render();
        } else {
            lostRenderer.render();
        }
    }
}
