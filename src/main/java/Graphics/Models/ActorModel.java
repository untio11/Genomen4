package Graphics.Models;

import GameState.Entities.Actor;
import Graphics.RenderEngine.RayTracing.TerrainLoader;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class ActorModel extends BaseModel {
    private Actor actor;
    private int model_SSBO;

    public ActorModel(int vaoID, int[] dataBufferIDs, int vertexCount) {
        super(vaoID, dataBufferIDs, vertexCount);
    }

    /**
     * Convert a BaseModel into an actor model with the same base properties.
     * @param actor The actor to be added to the base model.
     * @param baseModel The base model with the rendering data.
     */
    public ActorModel(Actor actor, BaseModel baseModel) {
        super(baseModel);
        this.model_SSBO = TerrainLoader.loadDataToBuffer(baseModel.position_data);
        this.actor = actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public int getModelSSBO() {
        return model_SSBO;
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(actor.get3DPosition(),
                actor.getRotX(), actor.getRotY(), actor.getRotZ(), scale);
        return transformationMatrix;
    }
}
