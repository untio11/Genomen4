package Graphics.RenderEngine.RayTracing;

import Graphics.RenderEngine.Scene;
import org.lwjgl.opengl.GL43;

import java.nio.FloatBuffer;

class TerrainLoader {
    /**
     * Bind the data in the given chunk to the SSBO binding points for the compute shader.
     * @param chunk The chunk to be rendered.
     * @return Return the pointer to the buffer containing the entire chunk.
     */
    static int load(Scene.Chunk chunk) {
        FloatBuffer vertex_data = RayTracer.createBuffer(chunk.getCoordinateStream());

        int vertex_buffer = GL43.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, vertex_buffer);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, vertex_data, GL43.GL_STATIC_DRAW);

        return vertex_buffer;
    }
}
