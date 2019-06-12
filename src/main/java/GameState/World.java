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
    private MapConfiguration mapConfig;

    private double randomStart;

    private long seed;
    private Random r;

    private World(MapConfiguration mapConfig, long seed) {
        this.mapConfig = mapConfig;
        this.width = mapConfig.getMapSize();
        this.height = mapConfig.getMapSize();
        this.seed = seed;

        r = new Random(seed);
        this.randomStart = r.nextDouble();

        this.data = new MapGenerator(mapConfig, r.nextLong()).generate();

        this.father = spawnActor(false);
        this.kidnapper = spawnActor(true);

        this.camera = new Camera(); // Camera will be put over the head later
        camera.setPosition(new Vector3f(width/2, 30.0f, height/2));

    }
    public void setCameraFather() {
        father.add(camera);
    }

    public void setCameraKidnapper() {
        kidnapper.add(camera);
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

    /**
     * Generate an instance of the world. Ensure that no world is current before calling this by using World.cleanWorld().
     * @throws IllegalStateException If there already is a current world.
     */
    public static void initWorld() throws IllegalStateException {
        Random r = new Random();
        instance = new World(MapConfigurations.getNormalMap(), r.nextLong());
    }

    public static void initWorld(MapConfiguration mapConfig) throws IllegalStateException {
        Random r = new Random();
        instance = new World(mapConfig, r.nextLong());
    }

    public static void initWorld(MapConfiguration mapConfig, long seed) throws IllegalStateException {
        instance = new World(mapConfig, seed);
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
        return new Position(r.nextInt(width), r.nextInt(height));
    }

    /**
     * Spawn an actor on a random tile.
     */
    private Actor spawnActor(boolean kidnapper) {
        Position spawn = new Position(0, 0);

        // TODO: Check if the actor spawns on a non-walkable tile

        double radians = this.randomStart * 2 * Math.PI;
        double radius = this.width / 2 - mapConfig.getStartRadius();

        double yOffset = Math.sin(radians) * radius;
        double xOffset = Math.cos(radians) * radius;

        if (kidnapper) {
            yOffset *= -1;
            xOffset *= -1;
        }

        double y = height * 1f / 2 + yOffset;
        double x = width * 1f / 2 + xOffset;

        spawn.y = (int) y;
        spawn.x = (int) x;

        return new Actor(
                this,
                0.5f,
                new Vector3f(spawn.x + 0.5f, spawn.y + 0.5f, 0f),
                new Vector3f(0f, 0f, 0f),
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
        float fatherSize = father.getSize() - 0.00001f;
        float kidnapperSize = kidnapper.getSize() - 0.00001f;
        return !(father.getPosition().x - fatherSize / 2 > kidnapper.getPosition().x + kidnapperSize / 2 ||
                father.getPosition().y - fatherSize / 2 > kidnapper.getPosition().y + kidnapperSize / 2 ||
                kidnapper.getPosition().x - kidnapperSize / 2 > father.getPosition().x + fatherSize / 2 ||
                kidnapper.getPosition().y - kidnapperSize / 2 > father.getPosition().y + fatherSize / 2);
    }

    public TileType getTileType(int x, int y) { return getTile(x, y).getType(); }

    public int getWidth() { return width; }

    public int getHeight() { return height; }

    public Actor getFather() { return father; }

    public Actor getKidnapper() { return kidnapper; }

    /**
     * Get the actors, in order
     * @return {Father, Kidnapper}
     */
    public Actor[] getActors() {
        return new Actor[] {father, kidnapper};
    };

    public Camera getCamera() { return this.camera; }

    public Tile[][] getTiles() {
        return data;
    }

    public MapConfiguration getMapConfig() {
        return this.mapConfig;
    }
}
