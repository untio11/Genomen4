package GameState;

public class Tile {

    private TileType type;
    private float height;

    public boolean isAccessible() {
        return type.isAccessible(type);
    }

    public float getHeight() {
        return height;
    }

    public TileType getType() {
        return type;
    }

    /**
     * Constructor for a tile required a type and a height.
     */
    Tile(TileType t, int height) {
        this.type = t;
        this.height = height;
    }

    @Override
    public String toString() {
        switch (type) {
            case GRASS:
                return "Grass";
            case SAND:
                return "Sand";
            case WATER:
                return "Water";
            case TREE:
                return "Tree";
            default:
                return "Shore";
        }
    }
}
