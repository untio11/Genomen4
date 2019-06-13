package Graphics.Models;

import Graphics.Animation.Animation;
import Graphics.Animation.Bone;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;

/**
 * Base model with all the necessary data for rendering
 */
public class BaseModel {
    private int vaoID = -1;
    // positions, normals, texture, indices:
    private int[] dataBufferIDs = {-1, -1, -1, -1};
    float[] position_data;
    private float[] normal_data;
    private float[] color_data;
    private float[] texture_data;
    private int[] index_data;
    private int textureID = -1;
    private int vertexCount = -1;
    float scale = 1f;

    private Bone rootBone;
    private int boneCount;
    private Animation animation;

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
    // for animated BaseModel
    public BaseModel(int vaoID, int[] dataBufferIDs, int vertexCount, Bone rootBone, int boneCount, Animation animation) {
        this.vaoID = vaoID;
        this.dataBufferIDs = dataBufferIDs;
        this.vertexCount = vertexCount;
        this.rootBone = rootBone;
        this.boneCount = boneCount;
        this.animation = animation;
    }

    protected BaseModel(BaseModel base_model) {
        vaoID = base_model.getVaoID();
        dataBufferIDs = base_model.getDataBufferIDs();
        position_data = base_model.getPosition_data();
        normal_data = base_model.getNormalData();
        color_data = base_model.getColorData();
        index_data = base_model.getIndexData();
        textureID = base_model.getTextureID();
        vertexCount = base_model.getVertexCount();
        texture_data = base_model.getTextureData();
        scale = base_model.getScale();
    }

    public float[] getTextureData() {
        return texture_data;
    }

    public void setTextureData(float[] texture_data) {
        this.texture_data = texture_data;
    }

    /**
     * Fetch the position data of the tile in _Model space_ coordinates
     * @return Array of coordinates in model space
     */
    public Bone getRootBone() {      return rootBone;    }

    public int getBoneCount() {        return boneCount;    }

    public Animation getAnimation() {        return animation;    }

    public float[] getPosition_data() {
        return position_data;
    }

    public void setPositionData(float[] position_data) {
        this.position_data = position_data;
    }

    public float[] getNormalData() {
        return normal_data;
    }

    public void setNormalData(float[] normal_data) {
        this.normal_data = normal_data;
    }

    public float[] getColorData() {
        return color_data;
    }

    public void setColorData(float[] color_data) {
        this.color_data = color_data;
    }

    public int[] getIndexData() {
        return index_data;
    }

    public void setIndexData(int[] index_data) {
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

    public int getVertexSSBO() {
        return dataBufferIDs[0];
    }

    public int getIndexSSBO() {
        return dataBufferIDs[3];
    }

    public float getScale() {
        return scale;
    }
}
