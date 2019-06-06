package Graphics.RenderEngine.RayTracing;

import Graphics.Models.TerrainModel;
import Graphics.RenderEngine.Scene;
import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

class TerrainLoader {
    static public int tringle_count;

    /**
     * Gets the index and position data of all the tiles inside all the visible chunks and puts in
     * @param chunks The chunks to be loaded.
     * @return Returns the pointers to the SSBOs in the following format: [vertexSSBO, indexSSBO]
     */
    static int[] loadChunksToSSBOs(Scene.Chunk[] chunks) {
        Stream<Float> coordinate_stream = Stream.of();
        Stream<Integer> index_stream = Stream.of();
        int index_counter = 0;
        tringle_count = 0;

        for (int i = 0; i < chunks.length; i++) {
            Scene.Chunk chunk = chunks[i];
            tringle_count += chunk.getTringleCount();
            Integer[] indices = ArrayUtils.toObject(chunk.getIndex_stream());

            for (int j = 0; j < indices.length; j++) {
                indices[j] += index_counter;
            }

            index_counter += chunk.getCoordinate_amount() / 4;

            coordinate_stream = Stream.concat(coordinate_stream, Arrays.stream(ArrayUtils.toObject(chunk.getCoordinateStream())));
            index_stream = Stream.concat(index_stream, Arrays.stream(indices));
        }

        int vertexSSBO = loadDataToBuffer(ArrayUtils.toPrimitive(coordinate_stream.toArray(Float[]::new)));
        int indexSSBO = loadDataToBuffer(ArrayUtils.toPrimitive(index_stream.toArray(Integer[]::new)));
        return new int[] {vertexSSBO, indexSSBO};
    }

    /**
     * Load the parsed data into a buffer to be used by the compute shader.
     * @param data The data to be done
     * @return Pointer to the buffer where the data is stored.
     */
    static int loadDataToBuffer(float[] data) {
        FloatBuffer data_buffer = RayTracer.createBuffer(data);
        int buffer_id = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, buffer_id);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, data_buffer, GL43.GL_STATIC_DRAW);
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0);

        return buffer_id;
    }

    /**
     * Load the parsed data into a buffer to be used by the compute shader.
     * @param data The data to be done
     * @return Pointer to the buffer where the data is stored.
     */
    static int loadDataToBuffer(int[] data) {
        IntBuffer data_buffer = RayTracer.createBuffer(data);
        int buffer_id = GL43.glGenBuffers();

        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, buffer_id);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, data_buffer, GL43.GL_STATIC_DRAW);
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, 0);

        return buffer_id;
    }
}
