package RenderEngine;

import GameState.Camera;
import GameState.Player;
import Models.TexturedModel;
import Shaders.StaticShader;
import Shaders.TerrainShader;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import terrains.Terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private static final float FOV = 70;
    private static final float NEAR_PLANE = 0.1f;
    private static final float FAR_PLANE = 1000;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private PlayerRenderer playerRenderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Player>> entities = new HashMap<TexturedModel, List<Player>>();
    private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        GL11.glEnable(GL11.GL_CULL_FACE);   //don't render the back faces (which you don't see anyway)
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        playerRenderer = new PlayerRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
    }

    public void render(Player player, Camera camera) {
        prepare();
        shader.start();
        //shader.loadLight(player);
        shader.loadViewMatrix(camera);
        playerRenderer.render(entities);
        shader.stop();

        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains);
        terrainShader.stop();

        entities.clear();
        terrains.clear();
    }

    public void processPlayer(Player player) {
        TexturedModel entityModel = player.getModel();
        List<Player> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(player);
        } else {
            List<Player> newBatch = new ArrayList<>();
            newBatch.add(player);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    public void cleanUp() {
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
