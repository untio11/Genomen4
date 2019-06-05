package Graphics.RenderEngine.TraditionalRendering;

import Graphics.Models.TerrainModel;
import Graphics.Shaders.TerrainShader;
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

    public void render(List<TerrainModel> terrains) {
        for (TerrainModel terrain : terrains) {
            prepareTerrain(terrain);
            loadModelMatrix(terrain);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTextureModel();
        }
    }

    public void render(Map<Integer, List<TerrainModel>> terrains) {
        for (Integer texture : terrains.keySet()) {
            List<TerrainModel> batch = terrains.get(texture);
            prepareTerrain(batch.get(0));

            for (TerrainModel terrain : batch) {
                loadModelMatrix(terrain);
                GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            unbindTextureModel();
        }
    }

    private void prepareTerrain(TerrainModel terrain) {
        GL30.glBindVertexArray(terrain.getVaoID());
        GL20.glEnableVertexAttribArray(0);  // position
        GL20.glEnableVertexAttribArray(1);  // texture coords
        bindTextures(terrain);
        //shader.loadShineVar
    }

    // binds textures for the shader
    private void bindTextures(TerrainModel terrain) {
        // binds terrain texture to sampler 0
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrain.getTextureID());
    }

    private void unbindTextureModel() {
        GL20.glDisableVertexAttribArray(0); // position
        GL20.glDisableVertexAttribArray(1); // texture coords
        GL30.glBindVertexArray(0);
    }

    private void loadModelMatrix(TerrainModel terrain) {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                new Vector3f(terrain.getX(), 0, terrain.getY()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(transformationMatrix);
    }
}
