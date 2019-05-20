package GameState;

public class World {
    public static final int TS = 16;
    private int tileW, tileH;
    private Tile[][] data;
    private Player father;
    private Player kidnapper;


    public World(int tileW, int tileH) {
        this.tileW = tileW;
        this.tileH = tileH;
        //todo: place players in position
        kidnapper = new Player(1, 2, true, this);
        father = new Player(8, 6, false, this);

        MapGenerator mg = new MapGenerator();
        data = mg.generate(tileW, tileH);
    }

    public boolean getCollision(int x, int y) {
        if (x < 0 || x >= tileW || y < 0 || y >= tileH) {
            return true;
        }
        return !data[x][y].isAccessible();
    }

    public boolean isPlayerCollision() {
        return !(father.getPosX() > kidnapper.getPosX() + kidnapper.getWidth() ||
                father.getPosY() > kidnapper.getPosY() + kidnapper.getHeight() ||
                kidnapper.getPosX() > father.getPosX() + father.getWidth() ||
                kidnapper.getPosY() > father.getPosY() + father.getHeight());
    }

    public TileType getTileType(int x, int y) {
        return data[x][y].getType();
    }

    public Tile getTile(int x, int y) {
        return data[x][y];
    }

    public Tile[][] getTiles() {
        return data;
    }

    public int getTileW() {
        return tileW;
    }

    public int getTileH() {
        return tileH;
    }

    public Player getFather() {
        return father;
    }

    public Player getKidnapper() {
        return kidnapper;
    }

}
