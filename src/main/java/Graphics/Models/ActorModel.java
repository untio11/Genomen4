package Graphics.Models;

import GameState.Entities.Actor;
import Graphics.Animation.Animation;
import Graphics.Animation.Animator;
import Graphics.Animation.Bone;
import Graphics.RenderEngine.RayTracing.TerrainLoader;
import Toolbox.Maths;
import org.joml.Matrix4f;

public class ActorModel extends BaseModel {
    private Actor actor;

    // skeleton
    private final Bone rootBone;
    private final int boneCount;

    private Matrix4f[] jointMatrices;

    private final Animator animator;

//    public ActorModel(int vaoID, int[] dataBufferIDs, int vertexCount) {
//        super(vaoID, dataBufferIDs, vertexCount);
//    }

    /**
     * Convert a BaseModel into an actor model with the same base properties.
     * @param actor The actor to be added to the base model.
     * @param baseModel The base model with the rendering data.
     */
    public ActorModel(Actor actor, BaseModel baseModel, Bone rootBone, int boneCount, Animation animation) {
        super(baseModel);
        this.actor = actor;
        this.rootBone = rootBone;
        this.boneCount = boneCount;
        animator = new Animator(this);
        animator.doAnimation(animation);
        rootBone.calcInverseBindTransform(new Matrix4f());
    }

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public Bone getRootBone() {  return rootBone; }

    public int getBoneCount() { return boneCount; }

    @Override
    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = Maths.createTransformationMatrix(actor.get3DPosition(),
                actor.getRotX(), actor.getRotY(), actor.getRotZ(), scale);
        return transformationMatrix;
    }

    /**
     * Instructs this entity to carry out a given animation. To do this it
     * basically sets the chosen animation as the current animation in the
     * {@link Animator} object.
     *
     * @param animation
     *            - the animation to be carried out.
     */
    public void doAnimation(Animation animation) {
        animator.doAnimation(animation);
    }

    /**
     * Updates the animator for this entity, basically updating the animated
     * pose of the entity. Must be called every frame.
     */
    public void update() {
        animator.update();
    }

    /**
     * Gets an array of the all important model-space transforms of all the
     * joints (with the current animation pose applied) in the entity. The
     * joints are ordered in the array based on their joint index. The position
     * of each joint's transform in the array is equal to the joint's index.
     *
     * @return The array of model-space transforms of the joints in the current
     *         animation pose.
     */
    public Matrix4f[] getJointTransforms() {
        //Matrix4f[]
        jointMatrices = new Matrix4f[boneCount];
        addJointsToArray(rootBone);
        return jointMatrices;
    }

    /**
     * This adds the current model-space transform of a joint (and all of its
     * descendants) into an array of transforms. The joint's transform is added
     * into the array at the position equal to the joint's index.
     *
     * @param headJoint
     *            - the current joint being added to the array. This method also
     *            adds the transforms of all the descendents of this joint too.
     */
    private void addJointsToArray(Bone headJoint) {
        jointMatrices[headJoint.index] = headJoint.getAnimationTransform();
        for (Bone childBone : headJoint.getChildren()) {
            addJointsToArray(childBone);
        }
    }
}
