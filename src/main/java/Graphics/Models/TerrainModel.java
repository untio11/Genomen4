package Graphics.Models;

import GameState.Tile;
import GameState.TileType;

public class TerrainModel extends BaseModel { // Note: could be more efficient with interfaces + composition instead of inheritance
    private static final float SIZE = 1;   //scale for the size of map
    private float x, y; // Location on the map in tile coordinates.
    private Tile tile;

    public TerrainModel(Tile tile, BaseModel base) {
        super(base);
        this.tile = tile;
        this.x = tile.getPosition().x * SIZE;
        this.y = tile.getPosition().y * SIZE;

        // Translate model-space coordinates to world space
        for (int i = 0; i < position_data.length; i++) {
            if (i % 4 == 0) { // x-coordinate
                this.position_data[i] += this.x;
            } else if (i % 4 == 2) { // z-coordinate
                this.position_data[i] += this.y;
            }
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getHeight() {
        return this.tile.getType() == TileType.TREE ? 1 : 0;
    }

    /**
     * Gets the coordinates of this terrain in _World space_ coordinates. Call super for model space coordinates.
     * @return Stream of coordinates in world space.
     */
    @Override
    public float[] getPosition_data() {
        return this.position_data;
    }
}
