package Graphics;

import Graphics.RenderEngine.Loader;
import Graphics.RenderEngine.MasterRenderer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {
    private long window; // The window handle
    private final int width;
    private final int height;

    private Set<Integer> pressedKeys; // To collect all pressed keys for processing
    private Inputhandler inputhandler;

    private static MasterRenderer renderer;
    private static Loader loader;

    public WindowManager() {
        width = 600;
        height = 600;
        inputhandler = new Inputhandler();
        pressedKeys = new HashSet<>();
    }

    /**
     * Add the pressed key to the pressedKey set.
     * @param window The window the callback got called from
     * @param key The key that was pressed
     * @param scancode The scancode of the key that was pressed
     * @param action Whether the key was pressed, released or repeated
     * @param mods Modifier keys like ctrl and alt.
     */
    private void KeyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            pressedKeys.add(key);
        } else if (action == GLFW_RELEASE) {
            pressedKeys.remove(key);
        }
    }

    /**
     * Start up the window and ensure that it is teared down properly on exit
     */
    public void run() {
        init();
        loop();
        clean();
    }

    /**
     * Initialize the window by setting up the callbacks and window properties, then initialize opengl.
     */
    private void init() {
        initGLFW();
        initGraphics();
    }

    private void initGLFW() {
        // Redirect errors to System.error for debugging
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) // Initialize GLFW
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_TRUE); // The window will minimize when out of focus and in full screen

        // We need at least openGL version 4.3 for the compute shaders.
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        // Create the window in windowed mode
        window = glfwCreateWindow(width, height, "Genomen 4", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Remember key state until it has been handled (AKA doesn't miss a key press)
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        glfwSetKeyCallback(window, this::KeyCallback);

        // Get the video mode to fetch the screen resolution
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos( // Center the window inside the screen
                window,
                (vidmode.width() - width) / 2,
                (vidmode.height() - height) / 2
        );

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
    }

    /**
     * Initialize openGL, the renderer and other stuff needed to draw something on the screen.
     */
    private void initGraphics() {
        GL.createCapabilities();
        loader = new Loader();
        renderer = new MasterRenderer();
    }

    private void clean() {
        renderer.cleanUp();
        loader.cleanUp();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            inputhandler.handleInput(pressedKeys);

            renderer.render();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }


    }
}
