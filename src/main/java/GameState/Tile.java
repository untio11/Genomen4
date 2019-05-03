package GameState;

public class Tile extends Entity {
    public enum TileType {
        TREE,
        FLOOR,
        WATER // Etc...
    }

    private TileType type;
    private float height;
    private boolean accessible;

    public boolean isAccessible() {
        // This might need to be extended later.
        return accessible;
    }

    public float getHeight() {
        return height;
    }

    public TileType getType() {
        return type;
    }

    /**
     * Default construction creates a floor tile of height 0 that is accessible
     */
    Tile() {
        this.type = TileType.FLOOR;
        this.height = 0;
        this.accessible = true;
    }

    /**
     * Initialize the tile with the following properties.
     * @param type The type of tile.
     * @param height The visual height of the tile.
     * @param accessible Whether an entity can enter this tile.
     */
    Tile(TileType type, float height, boolean accessible) {
        this.type = type;
        this.height = height;
        this.accessible = accessible;
    }
}
