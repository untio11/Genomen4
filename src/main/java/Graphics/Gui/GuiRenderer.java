package Graphics.Gui;

import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.Scene;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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

public class GuiRenderer {

    private static int vaoId, IndexVBO;
    private static int quadProgram;
    private GuiTexture guiTexture;
    private int transLoc;
    private float[] transMat = new float[16];
    private Matrix4f matrix;

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

    public GuiRenderer() {

    }

    public void init(Scene scene) {
        setupQuad();
        createQuadProgram();
        setupTexture(scene);
    }

    public void render(int angle) {
        guiTexture.setRx(angle);
        guiTexture.setRy(angle);
        renderQuad();
    }

    /**
     * Create the texture that the compute shader will draw to.
     */
    private void setupTexture(Scene scene) {
        guiTexture = scene.getGui();
    }

    /**
     * Add the quad vertices to the VBO to be rendered on screen
     */
    private void setupQuad() {
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

    private int loadShader(String filename, int type) {
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

        int shaderID = GL41C.glCreateShader(type);
        GL41C.glShaderSource(shaderID, shader_source);
        GL41C.glCompileShader(shaderID);
        return shaderID;
    }

    private void createQuadProgram() {
        int vertexshader = loadShader("src/main/java/Graphics/Shaders/guiVertexShader.txt", GL_VERTEX_SHADER);
        int fragmentshader = loadShader("src/main/java/Graphics/Shaders/guiFragmentShader.txt", GL_FRAGMENT_SHADER);

        quadProgram = glCreateProgram();
        glAttachShader(quadProgram, vertexshader);
        glAttachShader(quadProgram, fragmentshader);

        GL20.glBindAttribLocation(quadProgram, 0, "position"); // Position in (x,y,z,w)

        glLinkProgram(quadProgram);
        glValidateProgram(quadProgram);
        System.out.println("[QuadProgram]: " + GL20.glGetProgramInfoLog(quadProgram));
    }

    /**
     * Draw the full screen quad with the texture that was generated by the compute shader drawn on it.
     */
    private void renderQuad() {
        glUseProgram(quadProgram);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, guiTexture.getTexture());
        Matrix4f matrix = Maths.createTransformationMatrix(guiTexture.getPosition(), guiTexture.getScale(), guiTexture.getRx() ,guiTexture.getRy());
        transLoc = glGetUniformLocation(quadProgram, "transformationMatrix");
        GL41.glProgramUniformMatrix4fv(quadProgram, transLoc, false, matrix.get(transMat));
        //System.out.println(Arrays.toString(transMat));
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0); // Vertex position data
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, IndexVBO); // Index data


        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, quad_indices.length, GL11.GL_UNSIGNED_INT, 0);

        // Put everything back to default (deselect)
        glUseProgram(0);
        glActiveTexture(0);
        GL11.glDisable(GL11.GL_BLEND);
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

}
