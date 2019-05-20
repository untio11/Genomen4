package GameState;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.Entities.LightSource;
import org.joml.Vector3f;

import java.util.Random;

/**
 * Singleton class keeping track of the current state of the game. This way both the renderer and simulator (engine)
 * can easily access an instance of the world without any weird dependencies.
 */
public class World {
    private static World instance;
    private int width;      //unit: tiles
    private int height;     //unit: tiles
    private Tile[][] data; // Stores the tiles of the world. note that the first parameter is the height of the world.
    private LightSource[] lights; // Store the lights for stuff
    private Actor father;
    private Actor kidnapper;
    private Camera camera;

    private World(int width, int height) {
        this.width = width;
        this.height = height;

        this.data = new MapGenerator().generate(width, height);

        this.father = spawnActor(false);
        this.kidnapper = spawnActor(true);

        this.camera = new Camera(father.getPosition().add(0, 0, 10f)); // Put the camera above the fathers head
    }

    /**
     * Returns the current instance of the world.
     *
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

    /**
     * Returns the tiles in a more conventional notation with x preceding y.
     */
    public Tile getTile(int x, int y) {
        return data[y][x];
    }

    /**
     * Returns a random position in the world
     */
    private Position getRandomTile() {
        Random random = new Random();
        return new Position(random.nextInt(width), random.nextInt(height));
    }

    /**
     * Spawn an actor on a random tile.
     */
    private Actor spawnActor(boolean kidnapper) {
        //todo: place the actors in specific positions instead random, because it sometimes messes up the map generation due to the random player distance
        //find an accessible tile for the player spawn
        Position spawn = getRandomTile();
        while (getCollision(spawn.x, spawn.y)) {
            spawn = getRandomTile();
        }

        return new Actor(
                this,
                null,
                0.5f,
                new Vector3f(spawn.x + 0.5f, spawn.y + 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
                1,
                kidnapper
        );
    }

    /**
     * Returns whether a tile at (x,y) is accessible.
     */
    public boolean getCollision(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true;
        }
        return !getTile(x, y).isAccessible();
    }

    /**
     * Returns whether the two actors are colliding.
     */
    public boolean isPlayerCollision() {
        return !(father.getPosition().x - father.getSize() / 2 > kidnapper.getPosition().x + kidnapper.getSize() / 2 ||
                father.getPosition().y - father.getSize() / 2 > kidnapper.getPosition().y + kidnapper.getSize() / 2 ||
                kidnapper.getPosition().x - kidnapper.getSize() / 2 > father.getPosition().x + father.getSize() / 2 ||
                kidnapper.getPosition().y - kidnapper.getSize() / 2 > father.getPosition().y + father.getSize() / 2);
    }

    public TileType getTileType(int x, int y) { return getTile(x, y).getType(); }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public Actor getFather() { return father; }

    public Actor getKidnapper() { return kidnapper; }

    public Camera getCamera() { return this.camera; }
}
