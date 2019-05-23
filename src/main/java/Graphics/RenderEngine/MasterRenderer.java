package Graphics.RenderEngine;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.TileType;
import GameState.World;
import Graphics.Models.TexturedModel;
import Graphics.Shaders.ShaderProgram;
import Graphics.Shaders.StaticShader;
import Graphics.Terrains.Terrain;
import Graphics.Shaders.TerrainShader;
import Graphics.Textures.TerrainTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

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
    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();
    private ActorRenderer actorRenderer;

    private Camera camera;
    private Map<TexturedModel, List<Actor>> entities = new HashMap<TexturedModel, List<Actor>>();
    private Map<TerrainTexture, List<Terrain>> terrainMap = new HashMap<>();
    //private List<Terrain> terrains = new ArrayList<>();

    public MasterRenderer() {
        // Fetch the camera from the world
        this.camera = World.getInstance().getCamera();
        //don't render the back faces (which you don't see anyway)
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        createProjectionMatrix();
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        actorRenderer = new ActorRenderer(shader, projectionMatrix);
    }

    public void render(Camera camera) {
        this.camera = camera;
        prepare();
        // render entities
        shader.start();
        //shader.loadLight(player);
        shader.loadViewMatrix(camera);
        actorRenderer.render(entities);
        shader.stop();
        entities.clear();

        // render terrain
        terrainShader.start();
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrainMap);
        terrainShader.stop();
        terrainMap.clear();
    }

    public void processEntity(Actor actor) {
        TexturedModel entityModel = actor.getModel();
        List<Actor> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(actor);
        } else {
            List<Actor> newBatch = new ArrayList<>();
            newBatch.add(actor);
            entities.put(entityModel, newBatch);
        }
    }

    public void processTerrain(Terrain terrain) {
        //terrains.add(terrain);

        TerrainTexture texture = terrain.getTexture();
        List<Terrain> terrainBatch = terrainMap.get(texture);
        if (terrainBatch != null) {
            terrainBatch.add(terrain);
        } else {
            List<Terrain> newBatch = new ArrayList<>();
            newBatch.add(terrain);
            terrainMap.put(texture, newBatch);
        }
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
