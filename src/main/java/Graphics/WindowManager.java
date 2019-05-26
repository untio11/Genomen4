package Graphics;

import GameState.World;
import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.MasterRenderer;
import Graphics.RenderEngine.Scene;
import Graphics.Terrains.Terrain;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class WindowManager implements Runnable{
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private boolean running = false;
    private Thread thread = new Thread(this);

    private WindowGL windowGL;
    private long window; // The window handle

    private InputHandler inputhandler;
    private AbstractRenderer renderer; // TODO: Add extra layer above MasterRenderer (AbstractRenderer?) to cover normal rasterizition shading and raytracing
    private Scene scene;
    private World world;

    public WindowManager() {
        this.world = World.getInstance();
        this.inputhandler = new InputHandler(World.getInstance().getFather());
        this.windowGL = new WindowGL();
        this.window = windowGL.initGLFW();
        renderer = new MasterRenderer();
        this.scene = new Scene(this.world); // First do window gl and initglfw, otherwise no openGL context will be available
    }

    public void start() {
        thread.run();
    }

    private void close() {
        renderer.clean();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() {
        boolean render;
        double firstTime;
        double lastTime = System.nanoTime() / 1e9d;
        double passedTime;
        double unprocessedTime = 0;

        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        while (!glfwWindowShouldClose(window)) { // TODO: Have a genaral Renderer.render() function to call
            render = false;
            firstTime = System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;

            //in case the game freezes, the while loop tries to catch up by updating faster
            while (unprocessedTime >= UPDATE_CAP) {
                render = true;
                unprocessedTime -= UPDATE_CAP;

                //update game
                inputhandler.update(UPDATE_CAP, windowGL.getPressedKeys());

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    System.out.println(fps + "fps <=> " + (float) 1000/frames + "ms/frame");
                    frames = 0;
                }
            }

            if (render) {
                // render the given scene
                renderer.render(scene);
                glfwSwapBuffers(window); // swap the color buffers, that is: show on screen what is happening

                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();

                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
