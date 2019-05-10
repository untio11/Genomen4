import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {
    // The window handle
    private long window;
    private boolean fullscreen;
    private int xpos;
    private int ypos;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_AUTO_ICONIFY, GLFW_TRUE); // The window will minimize when out of focus and in full screen

        // Create the window
        window = glfwCreateWindow(300, 300, "Genomen 4", NULL, NULL);
        if ( window == NULL ) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE ) {
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            } else if (key == GLFW_KEY_F11 && action == GLFW_RELEASE) {
                final long current_monitor = glfwGetPrimaryMonitor();
                final GLFWVidMode mode = glfwGetVideoMode(current_monitor);

                if (fullscreen) {
                    glfwSetWindowMonitor(window, NULL, xpos, ypos, 300, 300, mode.refreshRate());
                } else {
                    glfwSetWindowMonitor(window, current_monitor, 0,0, mode.width(), mode.height(), mode.refreshRate());
                }

                fullscreen = !fullscreen;
            }
        });

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);
        glfwSetWindowSizeLimits(window, 720, 480, 1920, 1080);

        glfwSetWindowPosCallback(window, (long window, int new_posx, int new_posy) -> {
            if (new_posx > 0 && new_posy > 0) { // Don't update when going fullscreen (Hacky, but it works)
                this.xpos = new_posx;
                this.ypos = new_posy;
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            xpos = (vidmode.width() - pWidth.get(0)) / 2;
            ypos = (vidmode.height() - pHeight.get(0)) / 2;

            glfwSetWindowPos(
                    window,
                    xpos,
                    ypos
            );
        } // the stack frame is popped automatically
    }

    private void loop() {
        boolean done = false;



        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            // This line is critical for LWJGL's interoperation with GLFW's
            // OpenGL context, or any context that is managed externally.
            // LWJGL detects the context that is current in the current thread,
            // creates the GLCapabilities instance and makes the OpenGL
            // bindings available for use.
            GL.createCapabilities();

            // Set the clear color
            glClearColor(0.5f, 0.5f, 0.5f, 0.0f);


            if (!done && GL.getCapabilities().OpenGL43) {
                System.out.println("We can do compute shaders!");
                String renderer = glGetString(GL_RENDERER);
                String openGLVersion = glGetString(GL_VERSION);
                System.out.println("Renderer: " + renderer + "\nVersion: " + openGLVersion);
                done = true;
            }

            // We also want a render call here or something
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}