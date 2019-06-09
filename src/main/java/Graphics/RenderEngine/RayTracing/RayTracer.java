package Graphics.RenderEngine.RayTracing;

import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.Scene;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13C.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13C.glActiveTexture;
import static org.lwjgl.opengl.GL20C.*;
import static org.lwjgl.opengl.GL20C.glValidateProgram;
import static org.lwjgl.opengl.GL42C.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT;
import static org.lwjgl.opengl.GL42C.glMemoryBarrier;
import static org.lwjgl.opengl.GL43C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class RayTracer implements AbstractRenderer {
    private static int width, height;
    private static int work_x, work_y;
    private static int[] work_group_size = new int[3];

    // VAO, VBO & SSBO stuff
    private static int vaoId, IndexVBO;
    private static int vertexSSBO, indexSSBO, colorSSBO, offsetSSBO, topLeftSSBO;
    private static int tringlecount;

    // Shader stuff
    private static int rayProgram, quadProgram;
    private static int rayTexture;
    public static int[] chunk_count = new int[2]; // # horizontal visible chunks, # vertical visible chunks

    // Quad stuff
    private static float[] quad_vertices = {
            -1f,  1f, 0f, 1f, // 1/6 -> ID:0
            -1f, -1f, 0f, 1f, // 2   -> ID:1
             1f, -1f, 0f, 1f, // 3/4 -> ID:2
             1f,  1f, 0f, 1f, // 5   -> ID:3
    };
    private static int[] quad_indices = {
            0, 1, 2,
            2, 3, 0
    };

    // Camera stuff
    private static Vector3f camera;
    private static float old_x, old_z;
    private static float fov = 1.7f; // Camera to viewport distance. smaller fov => wider viewangle; fov=1.7 and camera height=6
    private static float[] transform = {
            1f,  0f,  0f, // Right
            0f,  0f, -1f, // Up
            0f, -1f,  0f  // Forward
    };

    public RayTracer(int _width, int _height) {
        setDimensions(_width, _height);
    }

    public static void setDimensions(int _width, int _height) {
        width = _width;
        height = _height;
        setupTexture();
    }

    public void init(Scene scene) {
        setupQuad();
        createQuadProgram();
        setupTexture();
        createRayProgram();
    }



    private static void executeRay() {
        work_x = (int) Math.ceil(width / (float) work_group_size[0]);// getNextPowerOfTwo(width  / work_group_size[0]);
        work_y = (int) Math.ceil(height / (float) work_group_size[1]); //getNextPowerOfTwo(height / work_group_size[1]);

        GL41.glProgramUniform3f(rayProgram, 0, camera.x, camera.y, camera.z);
        GL41.glProgramUniform1f(rayProgram, 1, fov);
        GL41.glProgramUniformMatrix3fv(rayProgram, 2, false, transform);

        glUseProgram(rayProgram);

        GL43.glProgramUniform1i(rayProgram, 3, tringlecount);
        GL43.glProgramUniform2iv(rayProgram, 4, chunk_count);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, vertexSSBO);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, colorSSBO);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, indexSSBO);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 5, offsetSSBO);
        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 6, topLeftSSBO);
        glDispatchCompute(work_x, work_y, 1);


        GL43.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 1, 0);
        glMemoryBarrier(GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);
    }

    /**
     * Prepare all the variables for rendering the given scene
     * @param scene The scene to be rendered.
     */
    private static void prepare(Scene scene) {
        camera = scene.getCamera().getPosition();
        if (camera.x != old_x || camera.z != old_z) { // TODO: Better: update based on loaded chunks!
            Scene.Chunk[] chunks = scene.getVisibileChunks(camera.x, camera.z);
            if (chunks == null) return; // Scene not updated, so just draw it as is
            int[] ssbos = TerrainLoader.loadChunksToSSBOs(chunks);
            System.out.println("(H,V): (" + chunk_count[0] + "," + chunk_count[1] + ")");
            old_x = camera.x;
            old_z = camera.z;
            vertexSSBO = ssbos[0];
            colorSSBO = ssbos[1];
            indexSSBO = ssbos[2];
            offsetSSBO = ssbos[3];
            topLeftSSBO = ssbos[4];
            tringlecount = TerrainLoader.tringle_count;
        }
    }

    public void render(Scene scene) { // TODO: loadVertexPositions the data from the scene object into the compute shader
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        prepare(scene);
        executeRay();
        renderQuad();
    }

    /**
     * Draw the full screen quad with the texture that was generated by the compute shader drawn on it.
     */
    private static void renderQuad() {
        glUseProgram(quadProgram);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, rayTexture);

        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0); // Vertex position data
        GL20.glEnableVertexAttribArray(1); // Vertex color data
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, IndexVBO); // Index data

        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, quad_indices.length, GL11.GL_UNSIGNED_INT, 0);

        // Put everything back to default (deselect)
        glUseProgram(0);
        glActiveTexture(0);
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    public void clean() {

    }

    /**
     * Add the quad vertices to the VBO to be rendered on screen
     */
    private static void setupQuad() {
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        FloatBuffer verticesBuffer = createBuffer(quad_vertices);
        IntBuffer indexBuffer = createBuffer(quad_indices);

        int vertexVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexVBO);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesBuffer, GL15.GL_STATIC_DRAW);

        GL30.glVertexAttribPointer(0, 4, GL30.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        IndexVBO = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, IndexVBO);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);

        GL30.glBindVertexArray(0);
    }

    private static void createQuadProgram() {
        int vertexshader = loadShader("src/main/java/Graphics/Shaders/quadVertexShader.glsl", GL_VERTEX_SHADER);
        int fragmentshader = loadShader("src/main/java/Graphics/Shaders/fragmentShader.glsl", GL_FRAGMENT_SHADER);

        quadProgram = glCreateProgram();
        glAttachShader(quadProgram, vertexshader);
        glAttachShader(quadProgram, fragmentshader);

        GL20.glBindAttribLocation(quadProgram, 0, "position_in"); // Position in (x,y,z,w)

        glLinkProgram(quadProgram);
        glValidateProgram(quadProgram);
        System.out.println("[QuadProgram]: " + GL20.glGetProgramInfoLog(quadProgram));
    }

    private static void createRayProgram() {
        int ray_shader = loadShader("src/main/java/Graphics/Shaders/raytracer.glsl", GL_COMPUTE_SHADER);
        System.out.println("[RayTracerShader]: " + GL43.glGetShaderInfoLog(ray_shader));

        rayProgram = glCreateProgram();
        glAttachShader(rayProgram, ray_shader);
        glLinkProgram(rayProgram);
        glValidateProgram(rayProgram);

        System.out.println("[RayTracerProgram]: " + GL43.glGetProgramInfoLog(rayProgram));
        GL20.glGetProgramiv(rayProgram, GL_COMPUTE_WORK_GROUP_SIZE, work_group_size);
    }

    /**
     * Create the texture that the compute shader will draw to.
     */
    private static void setupTexture() {
        rayTexture = glGenTextures();
        GL15.glActiveTexture(GL13.GL_TEXTURE0);
        GL15.glBindTexture(GL_TEXTURE_2D, rayTexture);
        GL15.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        GL15.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        GL15.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        GL15.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        GL15.glTexImage2D(GL_TEXTURE_2D, 0, GL30C.GL_RGBA32F, width, height, 0, GL_RGBA, GL_FLOAT, NULL);
        GL43C.glBindImageTexture(0, rayTexture, 0, false, 0, GL15C.GL_WRITE_ONLY, GL30C.GL_RGBA32F);
    }

    /**
     * Convert an array of floats to a FloatBuffer
     * @param data The data that should be put in the buffer
     * @return The buffer with the original data, flipped and ready
     */
    public static FloatBuffer createBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    /**
     * Convert an array of bytes to an IntBuffer
     * @param data The data that should be put in the buffer
     * @return The buffer with the original data, flipped and ready
     */
    public static IntBuffer createBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private static int getNextPowerOfTwo(int value) {
        int result = value;
        result -= 1;
        result |= result >> 16;
        result |= result >> 8;
        result |= result >> 4;
        result |= result >> 2;
        result |= result >> 1;
        return result + 1;
    }

    private static int loadShader(String filename, int type) {
        StringBuilder shader_source = new StringBuilder();
        String line = null;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while( (line = reader.readLine()) != null ) {
                shader_source.append(line);
                shader_source.append('\n');
            }
        } catch(IOException e) {
            throw new IllegalArgumentException("unable to loadVertexPositions shader from file ["+filename+"]");
        }

        int shaderID = GL43C.glCreateShader(type);
        GL43C.glShaderSource(shaderID, shader_source);
        GL43C.glCompileShader(shaderID);
        return shaderID;
    }
}
