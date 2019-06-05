package Graphics.Models;

import Toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Base model with all the necessary data for rendering
 */
public class BaseModel {
    protected int vaoID;
    protected int[] dataBufferIDs;
    protected int textureID;
    protected int vertexCount;
    protected float scale;

    /**
     * Set the initial model data for rendering.
     * @param vaoID ID of the VAO containing all the data of this model.
     * @param dataBufferIDs ID's of the buffers containing the data. For the raytracing.
     * @param vertexCount Amount of vertices of this model.
     */
    public BaseModel(int vaoID, int[] dataBufferIDs, int vertexCount) {
        this.vaoID = vaoID;
        this.dataBufferIDs = dataBufferIDs;
        this.vertexCount = vertexCount;
    }

    protected BaseModel() {

    }

    public void setTexture(int textureID) {
        this.textureID = textureID;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(new Vector3f(0f),
                0, 0, 0, scale);
        return transformationMatrix;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getTriangleCount() {
        return vertexCount / 3;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getTextureID() {
        return textureID;
    }

    public int[] getDataBufferIDs() {
        return dataBufferIDs;
    }

    public float getScale() {
        return scale;
    }
}
