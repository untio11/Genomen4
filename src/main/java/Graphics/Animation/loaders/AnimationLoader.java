package Graphics.Animation.loaders;

import Graphics.Animation.Bone;
import Toolbox.Maths;
import org.joml.Matrix4f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;

public class AnimationLoader {

    private Matrix4f globalInverseTransform;
    private Bone bones[];
    private Matrix4f 	boneTransforms[];
    private AINode root;
    private AIAnimation	animation;

    public AnimationLoader(AIAnimation animation, Bone[] bones, AINode root) {
        this.bones = bones;
        this.root = root;
        this.animation = animation;

        AIMatrix4x4 inverseRootTransform = root.mTransformation();
        Matrix4f    inverseRootTransformation = Maths.fromAssimpMatrix(inverseRootTransform);
        globalInverseTransform = inverseRootTransformation;

        boneTransforms = new Matrix4f[bones.length];

    }

    AINodeAnim FindNodeAnim(AIAnimation pAnimation, String NodeName)
    {
        for (int i = 0 ; i < pAnimation.mNumChannels(); i++) {
            AINodeAnim pNodeAnim = AINodeAnim.create(pAnimation.mChannels().get(i));

            if (pNodeAnim.mNodeName().dataString().equals(NodeName)) return pNodeAnim;
        }

        return null;
    }

    public void boneTransforms(float timeInSeconds)
    {
        Matrix4f Identity = new Matrix4f();//.InitIdentity();

        float TicksPerSecond = (float)(animation.mTicksPerSecond() != 0 ? animation.mTicksPerSecond() : 25.0f);
        float TimeInTicks = timeInSeconds * TicksPerSecond;
        float AnimationTime = (TimeInTicks % (float)animation.mDuration());

        //ReadNodeHeirarchy(AnimationTime, root, Identity);

//        boneTransforms.resize(m_NumBones);

        for (short i = 0 ; i < bones.length ; i++) {
            boneTransforms[i] = bones[i].getAnimationTransform();
        }
    }
}
