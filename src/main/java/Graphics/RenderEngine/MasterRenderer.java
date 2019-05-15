package Graphics.RenderEngine;

import GameState.Entities.Actor;
import GameState.Entities.Camera;
import GameState.World;
import Graphics.Models.TexturedModel;
import Graphics.Shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {
    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Map<TexturedModel, List<Actor>> entities = new HashMap<>();

    private Camera camera;

    public MasterRenderer() {
        // Fetch the camera from the world
        this.camera = World.getInstance().getCamera();
    }

    public void render() {
        renderer.prepare();
        shader.start();
        //shader.loadLight(player);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
    }

    public void processEntity(Actor player) {
        TexturedModel entityModel = player.getModel();
        List<Actor> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(player);
        } else {
            List<Actor> newBatch = new ArrayList<>();
            newBatch.add(player);
            entities.put(entityModel, newBatch);
        }
    }

    public void cleanUp() {
        shader.cleanUp();
    }
}
