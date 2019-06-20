package Graphics;

import Graphics.RenderEngine.RayTracing.RayTracer;
import Toolbox.FramerateLogger;
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
    private int pixelWidth, pixelHeight;
    private long window; // The window handle
    private Set<Integer> pressedKeys; // To collect all pressed keys f

    public WindowGL(int pixelWidth, int pixelHeight, float scale) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;

        pressedKeys = new HashSet<>();
        initGLFW();
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

        if (key == GLFW_KEY_ESCAPE) {
            FramerateLogger.close();
            glfwSetWindowShouldClose(window, true);
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
        if(System.getProperty("os.name").startsWith("Mac")){
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
            glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
            glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, 1);
        } else {
            glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
            glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        }


        // Create the window in windowed mode
        window = glfwCreateWindow(pixelWidth, pixelHeight, "Genomen 4", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Remember key state until it has been handled (AKA doesn't miss a key press)
        glfwSetInputMode(window, GLFW_STICKY_KEYS, GLFW_TRUE);
        glfwSetWindowAspectRatio(window, 16, 9);
        glfwSetKeyCallback(window, this::KeyCallback);
        glfwSetWindowSizeCallback(window, this::windowSizeCallback);

        // Get the video mode to fetch the screen resolution
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos( // Center the window inside the screen
                window,
                (vidmode.width() - pixelWidth) / 2,
                (vidmode.height() - pixelHeight) / 2
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
        this.pixelWidth = width;
        this.pixelHeight = height;

        if (GameContainerGL.RAY_TRACING) { // Make sure to notify the raytracer so it can upscale its working resolution
            RayTracer.setDimensions(width, height);
        }
    }

    public long getWindow() {
        return window;
    }

    public void update() {}
    public void display() {}
    public void close() {}
}
