package GameState;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.Entities.LightSource;
import org.joml.Vector3f;

/**
 * Singleton class keeping track of the current state of the game. This way both the renderer and simulator (engine)
 * can easily access an instance of the world without any weird dependencies.
 */
public class World {
    private static World instance;

    private int width;
    private int height;
    private float tile_width; // Used for rendering
    private Tile[][] data; // Stores the actual world data.
    private LightSource[] lights; // Store the lights for stuff
    private Actor father;
    private Actor kidnapper;
    private Camera camera;

    /**
     * Returns the current instance of the world.
     * @return The current instance of the world.
     * @throws IllegalStateException If the world has not been initialized
     */
    public static World getInstance() throws IllegalStateException {
        if (instance == null) {
            throw new IllegalStateException("Please initialize the world before you try to get an instance of it.");
        }
        return instance;
    }

    public static void initWorld(int width, int height) {
        instance = new World(width, height);
    }

    /**
     * Lazily destroy the world. Next time all class variables will be reset and a new world is generated.
     */
    public static void cleanWorld() {
        instance = null;
    }

    private World(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new MapGenerator().generate(width, height);
        this.father = spawnActor();
        this.kidnapper = spawnActor();

        this.camera = new Camera(father.getPosition().add(0, 0, 10f)); // Put the camera above the fathers head
    }

    private Position getRandomTile() {
        return new Position(
                (int)(Math.random() * width),
                (int)(Math.random() * height)
        );
    }

    private Actor spawnActor() {
        Position spawn = getRandomTile();

        return new Actor(
                null,
                new Vector3f(spawn.x, spawn.y, 0f),
                0f,
                0f,
                0f,
                1
        );
    }

    public Camera getCamera() {
        return this.camera;
    }

    public Tile[][] getTiles() {
        return this.data;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getTileWidth() {
        return tile_width;
    }

    public LightSource[] getLights() {
        return lights;
    }
}
