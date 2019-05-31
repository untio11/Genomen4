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

    private World(MapConfiguration mapConfig) {
        this.width = mapConfig.getMapSize();
        this.height = mapConfig.getMapSize();

        this.data = new MapGenerator(mapConfig).generate();

        this.father = spawnActor(false);
        this.kidnapper = spawnActor(true);

        this.camera = new Camera(); // Camera will be put over the head later
        camera.setPosition(new Vector3f(0.0f, 0.0f, 10.0f));
        father.add(camera);
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
     * @param width The width the world should have in tiles
     * @param height The height the world should have in tiles.
     * @throws IllegalStateException If there already is a current world.
     */

    public static void initWorld() throws IllegalStateException {
        if (instance != null) {
            throw new IllegalStateException("There is an instance of the world already. Clear it with World.cleanWorld() or fetch it with World.getInstance().");
        }
        instance = new World(MapConfigurations.getNormalMap());
    }

    public static void initWorld(MapConfiguration mapConfig) throws IllegalStateException {
        if (instance != null) {
            throw new IllegalStateException("There is an instance of the world already. Clear it with World.cleanWorld() or fetch it with World.getInstance().");
        }
        instance = new World(mapConfig);
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
        Position spawn = new Position(0, 0);
        if (kidnapper) {
            spawn.x = width - 5;
        } else {
            spawn.x = 5;
        }
        spawn.y = height / 2;

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

    public Actor[] getActors() {
        return new Actor[] {father, kidnapper};
    };

    public Camera getCamera() { return this.camera; }

    public Tile[][] getTiles() {
        return data;
    }
}
