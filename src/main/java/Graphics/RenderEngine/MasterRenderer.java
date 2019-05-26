package Graphics.RenderEngine;

import GameState.Entities.Camera;
import GameState.World;
import Graphics.Shaders.StaticShader;
import Graphics.Terrains.Terrain;
import Graphics.Shaders.TerrainShader;
import Graphics.Textures.TerrainTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Map;

public class MasterRenderer implements AbstractRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private TerrainRenderer terrainRenderer; // Can the renderers can be static?
    private TerrainShader terrainShader = new TerrainShader();
    private ActorRenderer actorRenderer;

    private Camera camera;

    public MasterRenderer() {
        // Fetch the camera from the world
        this.camera = World.getInstance().getCamera();
        //don't render the back faces (which you don't see anyway)
//        GL11.glEnable(GL11.GL_CULL_FACE);
//        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        actorRenderer = new ActorRenderer(shader, projectionMatrix);
    }

    public void init() {

    }

    // TODO: Make sure that this can just render a given scene
    public void render(Scene scene) {
        //this.camera = camera;
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
        GL11.glClearColor(1, 0, 0, 1);
    }

    private void createProjectionMatrix() {
        // TODO: compute aspectRatio
        float aspectRatio = 1f;
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
