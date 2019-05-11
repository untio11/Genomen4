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
        this.data = new Tile[width][height];
    }

    public void createFather() {
        father = new Player(1,1, false, (int) tile_width);
    }

    public void createKidnapper() {
        father = new Player(2,2, true, (int) tile_width);
    }

    public void setGrass(int x, int y) {
        Tile grass = new Tile(TileType.GRASS, (int) tile_width);
        data[x][y] = grass;
    }

    public void setTree(int x, int y) {
        Tile tree = new Tile(TileType.TREE, (int) tile_width);
        data[x][y] = tree;
    }

    public void randomTiles() {
        Tile grass = new Tile(TileType.GRASS, (int) tile_width);
        Tile tree = new Tile(TileType.TREE, (int) tile_width);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                setGrass(x,y);
            }
        }
        for (int i = 0; i < width; i++) {
            setTree(i,0);
            setTree(i, height - 1);
        }
        for (int i = 0; i < height; i++) {
            setTree(0,i);
            setTree(width - 1, i);
        }
    }

    public boolean getCollision(int x, int y) {
        if (x < 0 || x >= tile_width || y < 0 || y >= tile_width) {
            return true;
        }
        return data[x][y].isAccessible();
    }

    public boolean getPlayerCollision() {
        return false;
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
