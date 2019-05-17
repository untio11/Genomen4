package Graphics;

import GameState.Entities.Camera;
import GameState.MapGenerator;
import GameState.TileType;
import GameState.World;
import Graphics.RenderEngine.Loader;
import Graphics.RenderEngine.MasterRenderer;
import Graphics.Terrains.Terrain;
import Graphics.Textures.TerrainTexture;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.*;

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
    private static Camera camera;
    private List<Terrain> terrainList;

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
        camera = new Camera();
        initTileMap(loader);
    }

    /**
     * Generates the map, loads the terrainTextures and creates a terrainTile for every tile in the map.
     * Stores the terrainTiles into a terrainList, which is later processed in the loop
     * @param loader
     */
    private void initTileMap(Loader loader) {
        // Generate map
//        MapGenerator mapGenerator = new MapGenerator();
//        mapGenerator.generate(5, 5);
//        System.out.println(mapGenerator.toString());

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
        for(int r = 0; r< World.getInstance().getWidth(); r++) {
            for(int c=0; c<World.getInstance().getHeight(); c++) {
                TileType tileType = World.getInstance().getTileType(r, c);
                // get texture form hashmap, if it isn't there, use the backuptexture
                tileTexture = textureHashMap.getOrDefault(tileType, backupTexture);
                // add to terrainList, which will be processed in loop
                terrainList.add(new Terrain(r, c, loader, backupTexture));
            }
        }
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

            //process all terrains make in initTileMap()
                for (Terrain terrain : terrainList) {
                    renderer.processTerrain(terrain);
                }

            // render all processed models
            renderer.render(camera);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }


    }
}
