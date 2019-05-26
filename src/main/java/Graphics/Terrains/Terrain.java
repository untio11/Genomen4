package Graphics.Terrains;

import Graphics.Models.RawModel;
import Graphics.RenderEngine.Loader;
import GameState.MapGenerator;
import GameState.TileType;
import Graphics.Textures.TerrainTexture;

public class Terrain {

    private static final float SIZE = 1;   //scale for the size of map

    private float x;
    private float z;
    private int height;
    private RawModel model;
    private TerrainTexture texture;
    private int[] colorRGB;

    public Terrain(int gridX, int gridZ, int height, Loader loader, TerrainTexture texture, int[] color) {
        this.texture = texture;
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.height = height *5;
        this.colorRGB = color;
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

    public int[] getColor() {
        return colorRGB;
    }

    private RawModel generateTerrain(Loader loader){

        float[] vertices = {
                // Top side
                0, height, 0,   //V0
                0, height, 1,   //V1
                1, height, 1,   //V2
                1, height, 0,    //V3
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

        // TODO: normals not calculated correctly
        float[] normals = new float[vertices.length];
        for(int n=0; n<vertices.length;n+=3) {
            normals[n] = 0;
            normals[n++] = 1;
            normals[n+2] = 0;
        }

        float[] textureCoords = {
//                0,0,
//                0,1,
//                1,1,
//                1,0,

                0.25f, 0.25f, 0.25f, 0.75f, 0.75f, 0.75f, 0.75f, 0.25f,
                //left
                0,0,0,1,1,1,1,0,
                //0.25f,0.25f,0.25f,1,1,1,1,0.25f,
                //right
                0,0,0,1,1,1,1,0,
                //front
                0,0,0,1,1,1,1,0,
                //back
                0,0,0,1,1,1,1,0,
        };

        float[] color = new float[vertices.length];
        for(int c=0; c<color.length;c+=3) {
            color[c] = colorRGB[0];       //r
            color[c+1] = colorRGB[1];     //g
            color[c+2] = colorRGB[2];     //b
        }

        int[] indices = new int[vertices.length/4*6];
//                0,1,3,   //Top left triangle (V0,V1,V3)
//                3,1,2,   //Bottom right triangle (V3,V1,V2)

        int indexCount = 0;
        for(int i=0;i<indices.length;i+=6) {
            indices[i] = indexCount;
            indices[i+1] = indexCount+1;
            indices[i+2] = indexCount+3;
            indices[i+3] = indexCount+3;
            indices[i+4] = indexCount+1;
            indices[i+5] = indexCount+2;
            indexCount += 4;
        }

        return loader.loadToVAO(vertices, textureCoords, normals, color, indices);
    }
}
