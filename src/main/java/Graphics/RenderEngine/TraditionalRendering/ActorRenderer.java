package Graphics.RenderEngine.TraditionalRendering;

import GameState.Entities.Actor;
import Graphics.Models.ActorModel;
import Graphics.Models.BaseModel;
import Graphics.Shaders.StaticShader;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.List;

public class ActorRenderer {


    private StaticShader shader;

    public ActorRenderer(StaticShader shader, Matrix4f projectionMatrix) {
        this.shader = shader;
        shader.start();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
    }



    public void render(List<ActorModel> entities) {
        for(ActorModel model : entities) {
            prepareModel(model);
            setTransformationMatrix(model);
            setJointTransforms(model);
            shader.loadJointTransforms(model.getJointTransforms());

            GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            unbindTextureModel();
        }
    }

    private void prepareModel(ActorModel model) {
        GL30.glBindVertexArray(model.getVaoID());
        GL20.glEnableVertexAttribArray(0);  //position
        GL20.glEnableVertexAttribArray(1);  //texture
        GL20.glEnableVertexAttribArray(2);  //normals   ToDo: add for bones
        GL20.glEnableVertexAttribArray(3);  //bones
        GL20.glEnableVertexAttribArray(4);  //bone weights
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTextureID());

    }

    private void unbindTextureModel() {
        GL20.glDisableVertexAttribArray(0);     //position
        GL20.glDisableVertexAttribArray(1);     //texture
        GL20.glDisableVertexAttribArray(2);     //normals   ToDo: add for bones
        GL20.glDisableVertexAttribArray(3);     //bones
        GL20.glDisableVertexAttribArray(4);     //bone weights
        GL30.glBindVertexArray(0);
    }

    private void setTransformationMatrix(ActorModel model) {
        Actor actor = model.getActor();
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(
                actor.get3DPosition(), // Translation
                actor.getRotX()-90, actor.getRotZ(), actor.getRotY(), // Rotation
                model.getScale() // Scaling
        );

        shader.loadTransformationMatrix(transformationMatrix);
    }

    private void setJointTransforms(ActorModel model) {
        Actor actor = model.getActor();
        model.update();
        shader.loadJointTransforms(model.getJointTransforms());
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
