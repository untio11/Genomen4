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

public class WindowManager implements Runnable{


    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private boolean running = false;
    private Thread thread = new Thread(this);

    private WindowGL windowGL;
    private long window; // The window handle

    private InputHandler inputhandler;
    private MasterRenderer renderer;
    private Loader loader;
    private Camera camera;
    private List<Terrain> terrainList;
    private World world;



    public WindowManager(World world) {
        this.world = world;
        windowGL = new WindowGL();
        inputhandler = new InputHandler(World.getInstance().getFather());
        window = windowGL.initGLFW();
        GL.createCapabilities();
        loader = new Loader();
        renderer = new MasterRenderer();
        RawModel playerModel = OBJLoader.loadObjModel("player", loader);
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("playerTexture"));
        TexturedModel texturedPlayer = new TexturedModel(playerModel, playerTexture);
        world.getFather().setModel(texturedPlayer);
        initTileMap(loader);
        camera = new Camera(world.getFather());
        world.getFather().setScale(0.1f);
    }

    public void start() {
        thread.run();
    }


    /**
     * Generates the map, loads the terrainTextures and creates a terrainTile for every tile in the map.
     * Stores the terrainTiles into a terrainList, which is later processed in the loop
     * @param loader
     */
    private void initTileMap(Loader loader) {
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
//        for (TileType tileType:TileType.values()) {
//            for(int r = 0; r< World.getInstance().getWidth(); r++) {
//                for (int c = 0; c < World.getInstance().getHeight(); c++) {
//                    if( tileType == World.getInstance().getTileType(r, c)) {
//                    //TileType tileType = World.getInstance().getTileType(r, c);
//                        // get texture form hashmap, if it isn't there, use the backupTexture
//                        tileTexture = textureHashMap.getOrDefault(tileType, backupTexture);
//                        // add to terrainList, which will be processed in loop
//                        terrainList.add(new Terrain(r, c, loader, tileTexture));
//                    }
//                }
//            }
//        }

            for(int r = 0; r< World.getInstance().getWidth(); r++) {
                for (int c = 0; c < World.getInstance().getHeight(); c++) {

                        TileType tileType = World.getInstance().getTileType(r, c);
                        // get texture form hashmap, if it isn't there, use the backupTexture
                        tileTexture = textureHashMap.getOrDefault(tileType, backupTexture);
                    int height = 0;

                    if (!tileType.isAccessible(tileType)) {
                            height = 1;
                        }

                        // add to terrainList, which will be processed in loop
                        terrainList.add(new Terrain(r, c, height, loader, tileTexture));

                }
            }

    }

    private void close() {
        renderer.cleanUp();
        loader.cleanUp();

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

        while (!glfwWindowShouldClose(window)) {
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
                camera.updatePosition();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                    System.out.println(fps);
                }
            }

            if (render) {
                //process all terrains make in initTileMap()
                for (Terrain terrain : terrainList) {
                    renderer.processTerrain(terrain);
                }

                renderer.processEntity(world.getFather());

                // render all processed models
                renderer.render(camera);

                glfwSwapBuffers(window); // swap the color buffers

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
