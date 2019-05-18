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
    public static final int TS = 16;
    private int width;
    private int height;
    private int tileW, tileH;
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

    public static void initWorld(int width, int height, int tileW, int tileH) {
        instance = new World(width, height, tileW, tileH);
    }

    /**
     * Lazily destroy the world. Next time all class variables will be reset and a new world is generated.
     */
    public static void cleanWorld() {
        instance = null;
    }

    private World(int width, int height, int tileW, int tileH) {
        this.width = width;
        this.height = height;
        this.tileW = tileW;
        this.tileH = tileH;
        this.data = new MapGenerator().generate(width, height);
        this.father = spawnActor(false);
        this.kidnapper = spawnActor(true);

        this.camera = new Camera(father.getPosition().add(0, 0, 10f)); // Put the camera above the fathers head
    }

    private Position getRandomTile() {
        return new Position(
                (int)(Math.random() * width),
                (int)(Math.random() * height)
        );
    }

    private Actor spawnActor(boolean kidnapper) {
        Position spawn = getRandomTile();

        return new Actor(
                this,
                null,
                1,
                new Vector3f(spawn.x, spawn.y, 0f),
                new Vector3f(0f, 0f, 0f),
                1,
                kidnapper
        );
    }

    public Camera getCamera() {
        return this.camera;
    }

    public boolean getCollision(int x, int y) {
        if (x < 0 || x >= tileW || y < 0 || y >= tileH) {
            return true;
        }
        return !data[x][y].isAccessible();
    }

    public boolean isPlayerCollision() {
        return !(father.getPosition().x > kidnapper.getPosition().y + kidnapper.getSize() ||
                father.getPosition().y > kidnapper.getPosition().y + kidnapper.getSize() ||
                kidnapper.getPosition().x > father.getPosition().x + father.getSize() ||
                kidnapper.getPosition().y > father.getPosition().y + father.getSize());
    }

    public TileType getTileType(int x, int y) {
        return data[x][y].getType();
    }

    public int getTileW() {
        return tileW;
    }

    public int getTileH() {
        return tileH;
    }

    public Actor getFather() {
        return father;
    }

    public Actor getKidnapper() {
        return kidnapper;
    }

}
