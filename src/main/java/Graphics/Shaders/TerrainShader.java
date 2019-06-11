package Graphics.Shaders;

import GameState.Entities.Camera;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class TerrainShader extends ShaderProgram {


    private static final String VERTEX_FILE = "src/main/java/Graphics/Shaders/terrainVertexShader.glsl";
    private static final String FRAGMENT_FILE = "src/main/java/Graphics/Shaders/terrainFragmentShader.glsl";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_terrainTexture;


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
        location_terrainTexture = super.getUniformLocation("terrainTexture");
    }

    public void connectTextureUnits() {
        super.loadInt(location_terrainTexture, 0);
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
