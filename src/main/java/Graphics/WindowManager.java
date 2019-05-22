package Graphics;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.TileType;
import GameState.World;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.RenderEngine.Loader;
import Graphics.RenderEngine.MasterRenderer;
import Graphics.RenderEngine.OBJLoader;
import Graphics.Terrains.Terrain;
import Graphics.Textures.ModelTexture;
import Graphics.Textures.TerrainTexture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.util.*;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class WindowManager {
    private long window; // The window handle



    private InputHandler inputhandler;

    private WindowGL windowGL;


    private static MasterRenderer renderer;
    private static Loader loader;
    private static Camera camera;
    private List<Terrain> terrainList;
    private static Actor stall, player;


    public WindowManager(World world) {
        windowGL = new WindowGL();
        inputhandler = new InputHandler(World.getInstance().getFather());

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
        window = windowGL.initGLFW();
        initGraphics();
    }



    /**
     * Initialize openGL, the renderer and other stuff needed to draw something on the screen.
     */
    private void initGraphics() {
        GL.createCapabilities();
        loader = new Loader();
        renderer = new MasterRenderer();
        //camera = new Camera();

        //actorList = new ArrayList<>();
        /*RawModel stallModel = OBJLoader.loadObjModel("stall", loader);
        ModelTexture stallTexture = new ModelTexture(loader.loadTexture("stallTexture"));
        TexturedModel texturedStall = new TexturedModel(stallModel, stallTexture);
        stall = new Actor(World.getInstance(), texturedStall,1, new Vector3f(0,0,0), new Vector3f(0,0,0), 1, false);*/

        RawModel playerModel = OBJLoader.loadObjModel("player", loader);
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
        TexturedModel texturedPlayer = new TexturedModel(playerModel, playerTexture);
        // player = new Actor(World.getInstance(), texturedPlayer,1, new Vector3f(0,0,0), new Vector3f(0,0,0), 1, false);
        World.getInstance().getFather().setModel(texturedPlayer);
        // actorList.add(actor);

        initTileMap(loader);

        camera = new Camera(World.getInstance().getFather());
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
        for (TileType tileType:TileType.values()) {
            for(int r = 0; r< World.getInstance().getWidth(); r++) {
                for (int c = 0; c < World.getInstance().getHeight(); c++) {
                    if( tileType == World.getInstance().getTileType(r, c)) {
                        // get texture form hashmap, if it isn't there, use the backuptexture
                        tileTexture = textureHashMap.getOrDefault(tileType, backupTexture);
                        // add to terrainList, which will be processed in loop
                        terrainList.add(new Terrain(r, c, loader, tileTexture));
                    }
                }
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
            inputhandler.update(1/60f, windowGL.getPressedKeys());
            camera.updatePosition();



                //process all terrains make in initTileMap()
                for (Terrain terrain : terrainList) {
                    renderer.processTerrain(terrain);
                }
                
                renderer.processEntity(World.getInstance().getFather());
                // render all processed models
            renderer.render(camera);

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }


    }
}
