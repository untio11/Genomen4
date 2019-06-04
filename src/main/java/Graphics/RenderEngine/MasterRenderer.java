package Graphics.RenderEngine;

import GameState.Entities.Camera;
import Graphics.Shaders.StaticShader;
import Graphics.Terrains.Terrain;
import Graphics.Shaders.TerrainShader;
import Graphics.Textures.TerrainTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public class MasterRenderer implements AbstractRenderer {

    private static final float FOV = 100;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 300;

    private static Matrix4f projectionMatrix;

    private static StaticShader shader = new StaticShader();
    private static TerrainRenderer terrainRenderer; // Can the renderers can be static?
    private static TerrainShader terrainShader = new TerrainShader();
    private static ActorRenderer actorRenderer;

    private static Camera camera;

    public MasterRenderer() {
        createProjectionMatrix();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        actorRenderer = new ActorRenderer(shader, projectionMatrix);
    }

    public void init() { // TODO: should this just all be done in the constructor?

    }

    // TODO: Make sure that this can just render a given scene
    public void render(Scene scene) {
        camera = scene.getCamera();
        prepare();
        List<Model> entities = scene.getEntities();
        Map<TerrainTexture, List<Terrain>> terrain_map = scene.getTexture_to_terrainlist_map();

        // render entities
        shader.start();
        //shader.loadLight(player);
        shader.loadViewMatrix(camera);
        actorRenderer.render(entities);
        shader.stop();

        // render terrain
        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrain_map);
        terrainShader.stop();
    }

    public void clean() {
        shader.cleanUp();
        terrainShader.cleanUp();
    }

    public void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(0, 0.45f, 1.0f, 1);
    }

    private void createProjectionMatrix() {
        // TODO: compute aspectRatio
        float aspectRatio = 16/9f;
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustrum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix =  new Matrix4f();
        projectionMatrix._m00(x_scale);
        projectionMatrix._m11(y_scale);
        projectionMatrix._m22(-((FAR_PLANE + NEAR_PLANE) / frustrum_length));
        projectionMatrix._m23(-1);
        projectionMatrix._m32(-((2 * NEAR_PLANE * FAR_PLANE) / frustrum_length));
        projectionMatrix._m33(0);
    }
}
