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
    private int width;
    private int height;
    private Tile[][] data; // Stores the actual world data.
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

    public Tile getTile(int x, int y) {
        return data[y][x];
    }

    private Position getRandomTile() {
        Random random = new Random();
        return new Position(
                random.nextInt(width), random.nextInt(height)
        );
    }

    private Actor spawnActor(boolean kidnapper) {
        Position spawn = getRandomTile();
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

    public Camera getCamera() {
        return this.camera;
    }

    public boolean getCollision(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return true;
        }
        return !getTile(x, y).isAccessible();
    }

    public boolean isPlayerCollision() {
        return !(father.getPosition().x - father.getSize() / 2 > kidnapper.getPosition().x + kidnapper.getSize() / 2 ||
                father.getPosition().y - father.getSize() / 2 > kidnapper.getPosition().y + kidnapper.getSize() / 2 ||
                kidnapper.getPosition().x - kidnapper.getSize() / 2 > father.getPosition().x + father.getSize() / 2 ||
                kidnapper.getPosition().y - kidnapper.getSize() / 2 > father.getPosition().y + father.getSize() / 2);
    }

    public TileType getTileType(int x, int y) {
        return getTile(x, y).getType();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Actor getFather() {
        return father;
    }

    public Actor getKidnapper() {
        return kidnapper;
    }

}
