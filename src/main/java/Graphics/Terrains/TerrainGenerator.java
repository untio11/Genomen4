package Graphics.Terrains;

import GameState.Tile;
import GameState.TileType;
import Graphics.Models.BaseModel;
import Graphics.Models.TerrainModel;
import Graphics.RenderEngine.Loader;
import Graphics.RenderEngine.RayTracing.RayTracer;
import Graphics.WindowManager;
import util.Pair;

public class TerrainGenerator {
    private static final int COORDINATE_COUNT = WindowManager.RAY_TRACING ? 4 : 3;
    private static float height;

    public static TerrainModel generateTerrain(Tile tile, int texture, Loader loader) {
        height = (tile.getType() == TileType.TREE) ? 10 : 0;
        BaseModel base = generateTerrainBase(loader);
        base.setTexture(texture);
        return new TerrainModel(tile, base);
    }

    private static BaseModel generateTerrainBase(Loader loader) {
        float[] vertices = getVertices(); // Use 4 coordinate vertices when raytracing
        int vertex_count = vertices.length / COORDINATE_COUNT; // Not the same as amount of triangles

        float[] normals = new float[vertices.length];
        for (int n = 0; n < vertices.length; n++) { // All vertices have normals in the y-direction (up)
            if (WindowManager.RAY_TRACING) {
                normals[n] = (n % 2 == 1) ? 1 : 0; // (0, 1, 0, 1) for all vertices
            } else {
                normals[n] = (n % 3 == 1) ? 1 : 0; // (0, 1, 0) for all vertices
            }
        }

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0,
                //left
                0, 0, 0, 1, 1, 1, 1, 0,
                //right
                0, 0, 0, 1, 1, 1, 1, 0,
                //front
                0, 0, 0, 1, 1, 1, 1, 0,
                //back
                0, 0, 0, 1, 1, 1, 1, 0,
        };

        int[] indices = new int[(vertex_count / 4) * 6]; // 4 vertices -> 1 face -> 2 triangles -> 6 indices
        int indexCount = 0;

        for (int i = 0; i < indices.length; i += 6) {
            indices[  i  ] = indexCount;
            indices[i + 1] = indexCount + 1;
            indices[i + 2] = indexCount + 3;
            indices[i + 3] = indexCount + 3;
            indices[i + 4] = indexCount + 1;
            indices[i + 5] = indexCount + 2;
            indexCount += 4;
        }

        BaseModel baseModel = loader.loadToModel(vertices, textureCoords, normals, indices);
        baseModel.setIndex_data(indices);
        baseModel.setNormal_data(normals);
        baseModel.setPosition_data(vertices);
        baseModel.setTexture_data(textureCoords);
        return baseModel;
    }

    /**
     * Get the vertices of the terrain with the given amount of coordinates.
     * @return A list of coordinates for vertices of the terrain where coordinateCount defines the amount of coordinates per vertex.
     */
    private static float[] getVertices() {
        float[] result;
        float[] base;

        if (height > 0) {
            base = new float[] { // Generate the basic model based on the height.
                    0, height, 0,    //V0
                    0, height, 1,    //V1
                    1, height, 1,    //V2
                    1, height, 0,    //V3
                    // Left side
                    0, height, 0,    //V4
                    0, 0, 0,         //V5
                    0, 0, 1,         //V6
                    0, height, 1,    //V7
                    // Right side
                    1, height, 1,    //V8
                    1, 0, 1,         //V9
                    1, 0, 0,         //V10
                    1, height, 0,    //V11
                    // Front side
                    0, height, 1,    //V12
                    0, 0, 1,         //V13
                    1, 0, 1,         //V14
                    1, height, 1,    //V15
                    // Back side
                    0, height, 0,    //V12
                    0, 0, 0,         //V13
                    1, 0, 0,         //V14
                    1, height, 0     //V15
            };
        } else {
            base = new float[] { // Generate the basic model based on the height.
                    0, 0, 0,    //V0
                    0, 0, 1,    //V1
                    1, 0, 1,    //V2
                    1, 0, 0     //V3
            };
        }

        if (COORDINATE_COUNT == 3) {
            result = base;
        } else {
            result = new float[(base.length / 3) * 4];

            for (Pair<Integer, Integer> indices = new Pair<>(0, 0);
                 indices.getFirst() < base.length;
                 indices.setFirst(indices.getFirst() + 1)) {

                if (indices.getFirst() > 0 && (indices.getFirst()) % 3 == 0) {
                    result[indices.getSecond()] = 1f;
                    indices.setSecond(indices.getSecond() + 1);
                }

                result[indices.getSecond()] = base[indices.getFirst()];

                indices.setSecond(indices.getSecond() + 1);
            }

            result[result.length - 1] = 1f;
        }

        return result;
    }
}