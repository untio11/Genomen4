package Graphics.Terrains;

import Graphics.Models.RawModel;
import Graphics.RenderEngine.Loader;
import GameState.MapGenerator;
import GameState.TileType;
import Graphics.Textures.TerrainTexture;

public class Terrain {

    private static final float SIZE = 4;   //scale for the size of map
    private static final int VERTEX_COUNT = 64;

    private float x;
    private float z;
    private RawModel model;
    private TerrainTexture texture;

    public Terrain(int gridX, int gridZ, Loader loader, TerrainTexture texture) {
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.model = generateTerrain(loader);
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public RawModel getModel() {
        return model;
    }

    public TerrainTexture getTexture() {
        return texture;
    }

    // was used to calculate the offset for the texture coords, but now handeled by drawing wiles seperately
//    private int[] textureOffset(float posX, float posY) {
//        int[] worldDim = mapGenerator.getWorldDimensions();
//        float maxPosX = (float)VERTEX_COUNT * SIZE;
//        int tileX = (int)Math.ceil(posX / (maxPosX/worldDim[0]));
//        int tileY = (int)Math.ceil(posY / (maxPosX/worldDim[1]));
//
//        return getTextureOffset(mapGenerator.getMap()[tileX][tileY].getType());
//    }

    private int[] getTextureOffset(TileType tileType) {
        if(tileType == TileType.WATER) {
            return new int[] {0, 1};
        } else if (tileType == TileType.SAND) {
            return new int[] {1, 2};
        } else if (tileType == TileType.GRASS) {
            return new int[] {2, 1};
        } else if (tileType == TileType.TREE) {
            return new int[] {2, 2};
        } else {
            return new int[] {1, 1};
        }
    }

    private RawModel generateTerrain(Loader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
        int vertexPointer = 0;
        for(int i=0;i<VERTEX_COUNT;i++){
            for(int j=0;j<VERTEX_COUNT;j++){
                float posX = (float)j/((float)VERTEX_COUNT - 1);
                float posY = (float)i/((float)VERTEX_COUNT - 1);
                vertices[vertexPointer*3] = posX * SIZE;
                vertices[vertexPointer*3+1] = 0;
                vertices[vertexPointer*3+2] = posY * SIZE;
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
                textureCoords[vertexPointer*2] = posX;
                textureCoords[vertexPointer*2+1] = posY;
                vertexPointer++;
            }
        }
        int pointer = 0;
        for(int gz=0;gz<VERTEX_COUNT-1;gz++){
            for(int gx=0;gx<VERTEX_COUNT-1;gx++){
                int topLeft = (gz*VERTEX_COUNT)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadToVAO(vertices, textureCoords, normals, indices);
    }
}
