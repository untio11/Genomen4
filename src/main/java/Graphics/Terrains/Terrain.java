package Graphics.Terrains;

import Graphics.Models.RawModel;
import Graphics.RenderEngine.Loader;
import GameState.MapGenerator;
import GameState.TileType;
import Graphics.Textures.TerrainTexture;

public class Terrain {

    private static final float SIZE = 1;   //scale for the size of map
    private static final int VERTEX_COUNT = 2;

    private float x;
    private float z;
    private int height;
    private RawModel model;
    private TerrainTexture texture;

    public Terrain(int gridX, int gridZ, int height, Loader loader, TerrainTexture texture) { // TODO: Constructor can probably just take a Tile object for initialization instead of all these loose parameters
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.height = height * 10;
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
                float posZ = (float)i/((float)VERTEX_COUNT - 1);
                vertices[vertexPointer*3] = posX * SIZE;
                vertices[vertexPointer*3+1] = height * SIZE;
                vertices[vertexPointer*3+2] = posZ * SIZE;
                normals[vertexPointer*3] = 0;
                normals[vertexPointer*3+1] = 1;
                normals[vertexPointer*3+2] = 0;
                textureCoords[vertexPointer*2] = posX;
                textureCoords[vertexPointer*2+1] = posZ;
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

        float[] vertices2 = {
                0, height, 0,   //V0
                0, height, 1,   //V1
                1, height, 1,   //V2
                1, height, 0,   //V3
                // Left side
                0, height, 0,    //V4
                0, 0, 0,        //V5
                0, 0, 1,         //V6
                0, height, 1,    //V7
                // Right side
                1, height, 1,    //V8
                1, 0, 1,        //V9
                1, 0, 0,         //V10
                1, height, 0,    //V11
                // Front side
                0, height, 1,    //V12
                0, 0, 1,        //V13
                1, 0, 1,         //V14
                1, height, 1,    //V15
                // Back side
                0, height, 0,    //V12
                0, 0, 0,        //V13
                1, 0, 0,         //V14
                1, height, 0    //V15
        };

        float[] normals2 = new float[vertices2.length];
        for(int n=0; n<vertices2.length;n+=3) {
            normals2[n] = 0;
            normals2[n++] = 1;
            normals2[n+2] = 0;
        }

        float[] textureCoords2 = {
                0,0,
                0,1,
                1,1,
                1,0,
                //left
                0,0,0,1,1,1,1,0,
                //right
                0,0,0,1,1,1,1,0,
                //front
                0,0,0,1,1,1,1,0,
                //back
                0,0,0,1,1,1,1,0,
        };
//        for(int t=0; t<vertices2.length/3*2;t++) {
//            textureCoords2[t] =
//        }

        int[] indices2 = new int[vertices2.length/4*6];
//        = {
//                0,1,3,   //Top left triangle (V0,V1,V3)
//                3,1,2,   //Bottom right triangle (V3,V1,V2)
//                // Left side
//                4,5,7,
//                7,5,6,
//                // right
//
//                //front
//
//                //back
//        };

        int indexCount = 0;
        for(int i=0;i<indices2.length;i+=6) {
            indices2[i] = indexCount;
            indices2[i+1] = indexCount+1;
            indices2[i+2] = indexCount+3;
            indices2[i+3] = indexCount+3;
            indices2[i+4] = indexCount+1;
            indices2[i+5] = indexCount+2;
            indexCount += 4;
        }

        return loader.loadToVAO(vertices2, textureCoords2, normals2, indices2);
    }
}
