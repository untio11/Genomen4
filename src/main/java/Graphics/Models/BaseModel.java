package Graphics.Models;

import Toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Base model with all the necessary data for rendering
 */
public class BaseModel {
    private int vaoID = -1;
    private int[] dataBufferIDs = {-1, -1, -1, -1};
    private float[] position_data;
    private float[] normal_data;
    private float[] texture_data;
    private int[] index_data;
    private int textureID = -1;
    private int vertexCount = -1;
    float scale = 1f;

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

    protected BaseModel(BaseModel base_model) {
        vaoID = base_model.getVaoID();
        dataBufferIDs = base_model.getDataBufferIDs();
        position_data = base_model.getPosition_data();
        normal_data = base_model.getNormal_data();
        texture_data = base_model.getTexture_data();
        index_data = base_model.getIndex_data();
        textureID = base_model.getTextureID();
        vertexCount = base_model.getVertexCount();
        scale = base_model.getScale();
    }

    public float[] getPosition_data() {
        return position_data;
    }

    public void setPosition_data(float[] position_data) {
        this.position_data = position_data;
    }

    public float[] getNormal_data() {
        return normal_data;
    }

    public void setNormal_data(float[] normal_data) {
        this.normal_data = normal_data;
    }

    public float[] getTexture_data() {
        return texture_data;
    }

    public void setTexture_data(float[] texture_data) {
        this.texture_data = texture_data;
    }

    public int[] getIndex_data() {
        return index_data;
    }

    public void setIndex_data(int[] index_data) {
        this.index_data = index_data;
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
