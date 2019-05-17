import GameState.*;
import Models.RawModel;
import Models.TexturedModel;
import RenderEngine.Loader;
import RenderEngine.MasterRenderer;
import RenderEngine.OBJLoader;
import Textures.ModelTexture;
import Textures.TerrainTexture;
import Textures.TerrainTexturePack;
import org.joml.Vector3f;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import terrains.Terrain;

import java.nio.*;
import java.util.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main{

    // The window handle
    private long window;
    private final int width = 600;
    private final int length = 600;

    private Set<Integer> pressedKeys = new HashSet<>();

    private List<Terrain> terrainList;

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
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(width, length, "Hello World!", NULL, NULL);
        if ( window == NULL ) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        // Makes an array of all the keys that are being pressed
        checkInput();

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void initTileMap(Loader loader) {
        // Generate map
        MapGenerator mapGenerator = new MapGenerator();
        mapGenerator.generate(50, 50);
        System.out.println(mapGenerator.toString());

        // load terrain textures and put them in a hashmap
        HashMap<TileType, TerrainTexture> textureHashMap = new HashMap<>();
        // create textures
        TerrainTexture water = new TerrainTexture(loader.loadTexture("water"));
        TerrainTexture sand = new TerrainTexture(loader.loadTexture("sand"));
        TerrainTexture grass = new TerrainTexture((loader.loadTexture("grass")));
        TerrainTexture tree = new TerrainTexture((loader.loadTexture("tree")));
        // put them in the hashmap
        textureHashMap.put(TileType.WATER, water);
        textureHashMap.put(TileType.SAND, sand);
        textureHashMap.put(TileType.GRASS, grass);
        textureHashMap.put(TileType.TREE, tree);

        // backup texture
        TerrainTexture backupTexture = new TerrainTexture((loader.loadTexture("black")));

        // add every tile from map to a list, which is to be rendered
        terrainList = new ArrayList<>();
        TerrainTexture tileTexture;
        for(int r=0; r<mapGenerator.getWorldDimensions()[0]; r++) {
            for(int c=0; c<mapGenerator.getWorldDimensions()[1]; c++) {
                TileType tileType = mapGenerator.getMap()[r][c].getType();
                // get texture form hashmap, if it isn't there, use the backuptexture
                if (textureHashMap.containsKey(tileType)) {
                    tileTexture = textureHashMap.get(tileType);
                } else {
                    tileTexture = backupTexture;
                }
                // add to terrainList, which will be processed in loop
                terrainList.add(new Terrain(r, c, loader, tileTexture, mapGenerator));
            }
        }
    }

    private void loop() {

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        Loader loader = new Loader();

        initTileMap(loader);

        RawModel model = OBJLoader.loadObjModel("stall", loader);
        ModelTexture texture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedModel = new TexturedModel(model, texture);

        Player player = new Player(texturedModel, new Vector3f(0,0,0), 0, 0, 0, 1);

        Camera camera = new Camera();

        MasterRenderer renderer = new MasterRenderer();

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            for(int keyPressed : pressedKeys) {
                switch (keyPressed) {
                    case GLFW_KEY_W:
                        camera.moveUp();
                        break;
                    case GLFW_KEY_S:
                        camera.moveDown();
                        break;
                    case GLFW_KEY_A:
                        camera.moveLeft();
                        break;
                    case GLFW_KEY_D:
                        camera.moveRight();
                        break;
                }
            }

            //process all terrains make in initTileMap()
            for(Terrain terrain:terrainList) {
                renderer.processTerrain(terrain);
            }

            renderer.render(player, camera);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
//        shader.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
    }

    private void checkInput() {
        // Remember key state until it has been handled(AKA doesn't miss a key press)
        glfwSetInputMode(window, GLFW_STICKY_KEYS, 1);
        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if(action == GLFW_PRESS) {
                switch (key) {
                    case GLFW_KEY_W:
                        pressedKeys.add(GLFW_KEY_W);
                        break;
                    case GLFW_KEY_S:
                        pressedKeys.add(GLFW_KEY_S);
                        break;
                    case GLFW_KEY_A:
                        pressedKeys.add(GLFW_KEY_A);
                        break;
                    case GLFW_KEY_D:
                        pressedKeys.add(GLFW_KEY_D);
                        break;
                }
            } else if(action == GLFW_RELEASE) {
                switch (key) {
                    case GLFW_KEY_W:
                        pressedKeys.remove(GLFW_KEY_W);
                        break;
                    case GLFW_KEY_S:
                        pressedKeys.remove(GLFW_KEY_S);
                        break;
                    case GLFW_KEY_A:
                        pressedKeys.remove(GLFW_KEY_A);
                        break;
                    case GLFW_KEY_D:
                        pressedKeys.remove(GLFW_KEY_D);
                        break;
                }
            }
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true);
        });
    }

    public static void main(String[] args) {
        new Main().run();
    }

}