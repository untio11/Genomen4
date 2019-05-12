package RenderEngine;

import GameState.Camera;
import GameState.Entity;
import GameState.Player;
import Models.TexturedModel;
import Shaders.StaticShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    private StaticShader shader = new StaticShader();
    private Renderer renderer = new Renderer(shader);

    private Map<TexturedModel, List<Player>> entities = new HashMap<TexturedModel, List<Player>>();

    public void render(Player player, Camera camera) {
        renderer.prepare();
        shader.start();
        //shader.loadLight(player);
        shader.loadViewMatrix(camera);
        renderer.render(entities);
        shader.stop();
        entities.clear();
    }

    public void processEntity(Player player) {
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

    public void cleanUp() {
        shader.cleanUp();
    }
}