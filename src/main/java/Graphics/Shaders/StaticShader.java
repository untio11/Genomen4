package Graphics.Shaders;

import GameState.Entities.Camera;
import Graphics.Animation.shaderUniforms.UniformMat4Array;
import Graphics.Animation.shaderUniforms.UniformMatrix;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class StaticShader extends ShaderProgram {

    private static final int MAX_BONES = 12;    //max number of bones in the skeleton

    private static final String VERTEX_FILE = "src/main/java/Graphics/Animation/renderer/animatedEntityVertex.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/Graphics/Animation/renderer/animatedEntityFragment.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int[] location_jointTransforms = new int[12];

    protected UniformMatrix transformationMatrix = new UniformMatrix("transformationMatrix");
    protected UniformMatrix projectionMatrix = new UniformMatrix("projectionMatrix");
    protected UniformMatrix viewMatrix = new UniformMatrix("viewMatrix");
    protected UniformMat4Array jointTransforms = new UniformMat4Array("jointTransforms", MAX_BONES);

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
        super.storeAllUniformLocation(transformationMatrix, projectionMatrix, viewMatrix, jointTransforms);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normals");
        super.bindAttribute(3, "bones");
        super.bindAttribute(4, "boneWeights");
    }

    /* Old way of loading uniforms to shader*/
    @Override
    protected void getAllUniformLocations() {
//        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
//        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
//        location_viewMatrix = super.getUniformLocation("viewMatrix");
//        //    location_jointTransforms[i] = super.getUniformLocation("jointTransforms["+i+"]");
    }

    public void loadTransformationMatrix(Matrix4f transform) {
        transformationMatrix.loadMatrix(transform);
        //super.loadMatrix(location_transformationMatrix, transform);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        projectionMatrix.loadMatrix(projection);
        //super.loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f theViewMatrix = Maths.createViewMatrix(camera);
        viewMatrix.loadMatrix(theViewMatrix);
        //super.loadMatrix(location_viewMatrix, viewMatrix);
    }

    public void loadJointTransforms(Matrix4f[] transforms) {
        jointTransforms.loadMatrixArray(transforms);
        //super.loadMatrixArray(location_jointTransforms, transforms);
    }

}
