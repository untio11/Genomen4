package Textures;

import GameState.TileType;
import Graphics.Textures.TerrainTexture;

import java.util.HashMap;

public class TerrainTexturePack {

    private TerrainTexture water;
    private TerrainTexture sand;
    private TerrainTexture grass;
//    private TerrainTexture[] textures;
    private HashMap<TileType, TerrainTexture> textures;

    public TerrainTexturePack(HashMap<TileType, TerrainTexture> textures) {
        this.textures = textures;
    }

    public TerrainTexture getTexture(TileType tileType) {
        TerrainTexture texture = textures.get(tileType);
        return texture;
    }

    public TerrainTexture getWater() {
        return water;
    }

    public TerrainTexture getSand() {
        return sand;
    }
}
