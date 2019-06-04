package Graphics;

import Graphics.RenderEngine.RayTracer;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowGL {
    private int width;
    private int height;
    private long window; // The window handle
    private Set<Integer> pressedKeys; // To collect all pressed keys f

    public WindowGL() {
        width = 1600;
        height = 900;
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
    public void KeyCallback(long window, int key, int scancode, int action, int mods) {
        if (action == GLFW_PRESS) {
            pressedKeys.add(key);
        } else if (action == GLFW_RELEASE) {
            pressedKeys.remove(key);
        }
    }

    /**
     * Sets up a GLFW window with openGL context ready to use.
      * @return Pointer to the window.
     */
    long initGLFW() {
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
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);

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
        glfwSwapInterval(0);
        GL.createCapabilities();
        return window;
    }

    public Set<Integer> getPressedKeys() {
        return pressedKeys;
    }

    private void windowSizeCallback(long window, int width, int height) {
        GL11.glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
        if (WindowManager.RAY_TRACING) { // Make sure to notify the raytracer so it can upscale the resolution
            RayTracer.setDimensions(width, height);
        }
    }

    public long getWindow() {
        return window;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
