package Textures;

import terrains.Terrain;

public class TerrainTexturePack {

    private TerrainTexture water;
    private TerrainTexture sand;

    public TerrainTexturePack(TerrainTexture water, TerrainTexture sand) {
        this.water = water;
        this.sand = sand;
    }

    public TerrainTexture getWater() {
        return water;
    }

    public TerrainTexture getSand() {
        return sand;
    }
}
