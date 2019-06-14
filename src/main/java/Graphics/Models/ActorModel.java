package Graphics.Models;

import GameState.Entities.Actor;
import Graphics.RenderEngine.RayTracing.TerrainLoader;
import Toolbox.Maths;
import org.apache.commons.lang3.ArrayUtils;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ActorModel extends BaseModel {
    private Actor actor;
    private float[] circumscribing_spheres = null;
    int boundingSSBO = -1;

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
        this.actor = actor;
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    /**
     * Get the inscribing spheres for optimized intersection testing.
     * @return an array of vec4's (x,y,z,r)_i where the first where the first three coordinates denote the center
     * and r denotes the radius of the triangle at index i for the 0 <= i < n triangles in this model
     */
    public float[] getCircumScribingSpheres() {
        if (circumscribing_spheres == null) {
            Stream<Float> centers_and_radii = Stream.of();

            for (int i = 0; i < this.getTriangleCount(); i += 4) {
                centers_and_radii = Stream.concat(centers_and_radii, Arrays.stream(getCenterAndRadius(
                        new Vector3f(
                                position_data[index_data[  i  ]],
                                position_data[index_data[i + 1]],
                                position_data[index_data[i + 2]]
                        ), new Vector3f(
                                position_data[index_data[i + 3]],
                                position_data[index_data[i + 4]],
                                position_data[index_data[i + 5]]
                        ), new Vector3f(
                                position_data[index_data[i + 6]],
                                position_data[index_data[i + 7]],
                                position_data[index_data[i + 8]]
                        )
                )));
            }

            circumscribing_spheres = ArrayUtils.toPrimitive(centers_and_radii.toArray(Float[]::new));
        }

        return circumscribing_spheres;
    }

    private Float[] getCenterAndRadius(Vector3f v0, Vector3f v1, Vector3f v2) {
        Float[] result = new Float[4];

        Vector3f ac = v1.add(v0.negate(new Vector3f()), new Vector3f());
        Vector3f ab = v2.add(v0.negate(new Vector3f()), new Vector3f());
        Vector3f ab_cross_ac = ab.cross(ac, new Vector3f());
        // Here be dragons
        Vector3f aToCenter = ((ab_cross_ac.cross(ab, new Vector3f()).mul(ac.lengthSquared())).add(
                ac.cross(ab_cross_ac, new Vector3f()).mul(ab.lengthSquared()))).mul(1f / (2f * ab_cross_ac.lengthSquared()));
        float radius = aToCenter.length();
        Vector4f temp = new Vector4f(aToCenter.add(v0, new Vector3f()), 1);
        getTransformationMatrix().transform(temp);
        result[0] = temp.x;
        result[1] = temp.y;
        result[2] = temp.z;
        result[3] = radius;
        return result;
    }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(actor.get3DPosition(),
                actor.getRotX(), actor.getRotY(), actor.getRotZ(), scale);
        return transformationMatrix;
    }

    public int getBoundingSSBO() {
        if (boundingSSBO == -1) {
            boundingSSBO = TerrainLoader.loadDataToBuffer(getCircumScribingSpheres());
        }

        return boundingSSBO;
    }
}
