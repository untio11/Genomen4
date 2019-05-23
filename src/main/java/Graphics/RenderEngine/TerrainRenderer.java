package Graphics.RenderEngine;

import GameState.TileType;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.Terrains.Terrain;
import Graphics.Shaders.TerrainShader;
import Graphics.Textures.TerrainTexture;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class TerrainRenderer {

    private TerrainShader shader;

    public TerrainRenderer(TerrainShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.connectTextureUnits();
        shader.stop();
    }

    public void render(List<Terrain> terrains) {
        for (Terrain terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTextureModel();
        }
    }

    public void render(Map<TerrainTexture, List<Terrain>> terrains) {
        for(TerrainTexture texture:terrains.keySet()) {
            List<Terrain> batch = terrains.get(texture);
            prepareTerrain(batch.get(0));
            for (Terrain terrain:batch) {
                loadModelMatrix(terrain);
                GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTextureModel();
        }
    }

    private void prepareTerrain(Terrain terrain) {
        RawModel model = terrain.getModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);  // position
        GL20.glEnableVertexAttribArray(1);  // texture coords
        bindTextures(terrain);
        //shader.loadShineVar

    }

    // binds textures for the shader
    private void bindTextures(Terrain terrain) {
        // binds terrain texture to sampler 0
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTexture().getTextureID());
        // binds second texture to sampler 1 (but not used now)
//        GL13.glActiveTexture(GL13.GL_TEXTURE1);
//        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturePack.getSand().getTextureID());
    }

    private void unbindTextureModel() {
        GL20.glDisableVertexAttribArray(0); // position
        GL20.glDisableVertexAttribArray(1); // texture coords
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(Terrain terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), 0, terrain.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
