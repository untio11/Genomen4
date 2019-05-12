package Shaders;

import GameState.Camera;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class TerrainShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/main/java/Shaders/terrainVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/main/java/Shaders/terrainFragmentShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_waterTexture;
    private int location_sandTexture;


    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {

        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");

    }

    @Override
    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_waterTexture = super.getUniformLocation("waterTexture");
        location_sandTexture = super.getUniformLocation("sandTexture");
    }

    public void connectTextureUnits() {
        super.loadInt(location_waterTexture, 0);
        super.loadInt(location_sandTexture, 1);
    }

    public void loadTransformationMatrix(Matrix4f transform) {
        super.loadMatrix(location_transformationMatrix, transform);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        super.loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
    }
}
