package Graphics.RenderEngine;

import Graphics.Animation.Animation;
import Graphics.Animation.Bone;
import Graphics.Models.BaseModel;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class Loader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public BaseModel loadToModel(float[] positions, float[] textureCoords, float[] normals, int[] indices, int[] bones, float[] weights, Bone rootBone, int boneCount, Animation animation) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        storeDataInAttributeListInt(3, 4, bones);
        storeDataInAttributeList(4, 4, weights);
        unbindVAO();

        int[] bufferIDs = {
            storeDataInBareBuffer(positions),
            storeDataInBareBuffer(normals),
            storeDataInBareBuffer(textureCoords),
            storeDataInBareBuffer(indices),
                storeDataInBareBuffer(bones),
                storeDataInBareBuffer(weights)
        };

        return new BaseModel(vaoID, bufferIDs, indices.length, rootBone, boneCount, animation);
    }

    /**
     * Loads the terrain, no need for bones
     */
    public BaseModel loadToModel(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();

        int[] bufferIDs = {
                storeDataInBareBuffer(positions),
                storeDataInBareBuffer(normals),
                storeDataInBareBuffer(textureCoords),
                storeDataInBareBuffer(indices)
        };

        BaseModel result =  new BaseModel(vaoID, bufferIDs, indices.length);
        //result.setPositionData(positions);
        return result;
    }


    public int loadTexture(String fileName) {
        int textureID;
        int width, height;
        ByteBuffer image;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            image = stbi_load("res/" + fileName + ".png", w, h, comp, 4);
            if (image == null) {
                System.out.println("Failed to load texture file: " + fileName + "\n" +
                        stbi_failure_reason()
                );
            }
            width = w.get();
            height = h.get();
        }

        textureID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        textures.add(textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); //sets MINIFICATION filtering to nearest
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); //sets MAGNIFICATION filtering to nearest
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, image);

        return textureID;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL15.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attrNum, int coordSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attrNum, coordSize, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void storeDataInAttributeListInt(int attrNum, int coordSize, int[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL30.glVertexAttribIPointer(attrNum, coordSize, GL11.GL_INT, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    /**
     * Store the given data into a buffer to be used by the compute shader and return th ID to that buffer.
     * @param data The data to be put in the buffer.
     * @return Pointer to the generated buffer.
     */
    private int storeDataInBareBuffer(float[] data) {
        int SSBOID = GL15.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, SSBOID);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, storeDataInFloatBuffer(data), GL43.GL_STATIC_DRAW);
        return  SSBOID;
    }

    /**
     * Store the given data into a buffer to be used by the compute shader.
     * @param data The data to be put in the buffer.
     * @return Pointer to the generated buffer.
     */
    private int storeDataInBareBuffer(int[] data) {
        int SSBOID = GL15.glGenBuffers();
        GL43.glBindBuffer(GL43.GL_ARRAY_BUFFER, SSBOID);
        GL43.glBufferData(GL43.GL_ARRAY_BUFFER, storeDataInIntBuffer(data), GL43.GL_STATIC_DRAW);
        return  SSBOID;
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
