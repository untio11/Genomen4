package Graphics.RenderEngine;

import GameState.Entities.Actor;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.Textures.ModelTexture;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class Model {
    private Actor actor;
    private TexturedModel model;
    private float scale;

    public Model(Actor actor, RawModel playerModel, ModelTexture modelTexture, float scale) {
        this.model = new TexturedModel(playerModel, modelTexture);
        this.actor = actor;
    }

    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(actor.get3DPosition(),
                actor.getRotX(), actor.getRotY(), actor.getRotZ(), scale);
        return transformationMatrix;
    }

    public TexturedModel getModel() {
        return model;
    }

    public Actor getActor() {
        return actor;
    }
}
