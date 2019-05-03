package GameState;

public class World {
    private int width;
    private int height;
    private float tile_width; // Used for rendering
    private Tile[][] data; // Stores the actual world data.
    private LightSource[] lights; // Store the lights for stuff
    private Player father;
    private Player kidnapper;

    public World(int width, int height) {
        this.width = width;
        this.height = height;
        this.data = new Tile[height][width];
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
