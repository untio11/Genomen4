package Graphics.RenderEngine;

import GameState.Entities.Actor;
import Graphics.Models.RawModel;
import Graphics.Models.TexturedModel;
import Graphics.Textures.ModelTexture;
import Toolbox.Maths;
import org.joml.Matrix4f;

/**
 * Links a textured model to an actor
 */
public class Model {
    private Actor actor;
    private TexturedModel model;
    private float scale;

    /**
     * Setup the link between the actor and the textured model
     * @param actor The actor
     * @param texturedModel The textured model that should be linked to this actor
     * @param scale The scale of the model
     */
    Model(Actor actor, TexturedModel texturedModel, float scale) {
        this.model = texturedModel;
        this.actor = actor;
        this.scale = scale;
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

    public float getScale() {
        return scale;
    }
}
