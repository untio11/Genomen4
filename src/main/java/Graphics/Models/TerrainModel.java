package Graphics.Models;

import GameState.Tile;

public class TerrainModel extends BaseModel { // Note: could be more efficient with interfaces + composition instead of inheritance
    private static final float SIZE = 1;   //scale for the size of map
    private float x, y; // Location on the map in tile coordinates.

    public TerrainModel(Tile tile, BaseModel base) {
        super(base.getVaoID(), base.getDataBufferIDs(), base.getVertexCount());
        setTexture(base.getTextureID());
        setScale(base.getScale());
        this.x = tile.getColumn() * SIZE;
        this.y = tile.getRow() * SIZE;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
