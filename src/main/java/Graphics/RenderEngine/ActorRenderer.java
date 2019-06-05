package Graphics.RenderEngine;

import GameState.Entities.Actor;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.Shaders.StaticShader;
import Graphics.Textures.ModelTexture;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;
import java.util.Map;

public class ActorRenderer {


    private StaticShader shader;

    public ActorRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }



    public void render(List<Model> entities) {
        for(Model model : entities) {
            TexturedModel textured_model = model.getModel();
            prepareTextureModel(textured_model);
            prepareInstance(model);
            GL11.glDrawElements(GL11.GL_TRIANGLES, textured_model.getRawModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTextureModel();
        }
    }

    private void prepareTextureModel(TexturedModel texturedModel) {
        RawModel model = texturedModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        //ModelTexture texture = model.getTexture();
        //shader.loadShineVar
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());

    }

    private void unbindTextureModel() {
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void prepareInstance(Model model) {
        Actor actor = model.getActor();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(actor.get3DPosition(),
                actor.getRotX(), actor.getRotY(), actor.getRotZ(), model.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
    }

    /** Old renderer method, now divided in multiple methods (see above)
    public void render(Player player, StaticShader shader) {
        TexturedModel texturedModel = player.getModel();
        RawModel model = texturedModel.getRawModel();
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(player.getPosition(),
                player.getRotX(), player.getRotY(), player.getRotZ(), player.getScale());
        shader.loadTransformationMatrix(transformationMatrix);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getTextureID());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }
     */



}
