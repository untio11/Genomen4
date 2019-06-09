package Graphics.Animation;

import Toolbox.Maths;
import org.apache.commons.math3.complex.Quaternion;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.AIAnimation;
import org.lwjgl.assimp.AINode;
import org.lwjgl.assimp.AINodeAnim;

public class AnimationCalc {

    public Matrix4f 	globalInverseTransform;
    public Bone			bones[];
    public Matrix4f 	boneTransforms[];
    public AINode		root;
    public AIAnimation	animation;
//    public MeshResource resource;
//    public Shader		shader;
//    public Material		material;

    long timer = System.currentTimeMillis();

//    public void AddVertices(FloatBuffer vertices, IntBuffer indices)
//    {
//        shader = new Shader("forward-ambient2");
//        material = new Material(new Texture("bricks.jpg"), 1, 8,
//                new Texture("bricks_normal.jpg"), new Texture("bricks_disp.png"), 0.03f, -0.5f);
//
//        resource = new MeshResource(indices.capacity());
//
//        glBindBuffer(GL_ARRAY_BUFFER, resource.GetVbo());
//        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
//
//        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, resource.GetIbo());
//        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);
//    }

    public void update() {
        boneTransforms((float)(((double)System.currentTimeMillis() - (double)timer) / 1000.0));
    }

    AINodeAnim FindNodeAnim(AIAnimation pAnimation, String NodeName)
    {
        for (int i = 0 ; i < pAnimation.mNumChannels(); i++) {
            AINodeAnim pNodeAnim = AINodeAnim.create(pAnimation.mChannels().get(i));

            if (pNodeAnim.mNodeName().dataString().equals(NodeName)) return pNodeAnim;
        }

        return null;
    }

    void CalcInterpolatedPosition(Vector3f Out, float AnimationTime, AINodeAnim pNodeAnim)
    {
        if (pNodeAnim.mNumPositionKeys() == 1) {
            Out.set(Maths.fromAssimpVector(pNodeAnim.mPositionKeys().get(0).mValue()));
            return;
        }

        int PositionIndex = FindPosition(AnimationTime, pNodeAnim);
        int NextPositionIndex = (PositionIndex + 1);
        assert(NextPositionIndex < pNodeAnim.mNumPositionKeys());
        float DeltaTime = (float)(pNodeAnim.mPositionKeys().get(NextPositionIndex).mTime() - pNodeAnim.mPositionKeys().get(PositionIndex).mTime());
        float Factor = (AnimationTime - (float)pNodeAnim.mPositionKeys().get(PositionIndex).mTime()) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Vector3f Start = Maths.fromAssimpVector(pNodeAnim.mPositionKeys().get(PositionIndex).mValue());
        Vector3f End = Maths.fromAssimpVector(pNodeAnim.mPositionKeys().get(NextPositionIndex).mValue());
        Vector3f Delta = End.sub(Start);
        Out.set(Start.add(Delta.mul(Factor)));// + Factor * Delta;
    }


    void CalcInterpolatedRotation(Quaternion Out, float AnimationTime, AINodeAnim pNodeAnim)
    {
        // we need at least two values to interpolate...
        if (pNodeAnim.mNumRotationKeys() == 1) {
            Out = Maths.fromAssimpQuat(pNodeAnim.mRotationKeys().get(0).mValue());  //Out.set(..);
            return;
        }

        int RotationIndex = FindRotation(AnimationTime, pNodeAnim);
        int NextRotationIndex = (RotationIndex + 1);
        assert(NextRotationIndex < pNodeAnim.mNumRotationKeys());
        float DeltaTime = (float)(pNodeAnim.mRotationKeys().get(NextRotationIndex).mTime() - pNodeAnim.mRotationKeys().get(RotationIndex).mTime());
        float Factor = (AnimationTime - (float)pNodeAnim.mRotationKeys().get(RotationIndex).mTime()) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Quaternion StartRotationQ = Maths.fromAssimpQuat(pNodeAnim.mRotationKeys().get(RotationIndex).mValue());
        Quaternion EndRotationQ   = Maths.fromAssimpQuat(pNodeAnim.mRotationKeys().get(NextRotationIndex).mValue());
        Out = Maths.interpolate(StartRotationQ, EndRotationQ, Factor);    //StartRotationQ.slerp(EndRotationQ, Factor, false));// = AIQuaternion.Interpolate(Out, StartRotationQ, EndRotationQ, Factor);
//        Out = Out.Normalize();
    }


    void CalcInterpolatedScaling(Vector3f Out, float AnimationTime, AINodeAnim pNodeAnim)
    {
        if (pNodeAnim.mNumScalingKeys() == 1) {
            Out = Maths.fromAssimpVector(pNodeAnim.mScalingKeys().get(0).mValue());
            return;
        }

        int ScalingIndex = FindScaling(AnimationTime, pNodeAnim);
        int NextScalingIndex = (ScalingIndex + 1);
        assert(NextScalingIndex < pNodeAnim.mNumScalingKeys());
        float DeltaTime = (float)(pNodeAnim.mScalingKeys().get(NextScalingIndex).mTime() - pNodeAnim.mScalingKeys().get(ScalingIndex).mTime());
        float Factor = (AnimationTime - (float)pNodeAnim.mScalingKeys().get(ScalingIndex).mTime()) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Vector3f Start = Maths.fromAssimpVector(pNodeAnim.mScalingKeys().get(ScalingIndex).mValue());
        Vector3f End   = Maths.fromAssimpVector(pNodeAnim.mScalingKeys().get(NextScalingIndex).mValue());
        Vector3f Delta = End.sub(Start);
        Out.set(Start.add(Delta.mul(Factor)));
    }

    int FindPosition(float AnimationTime, AINodeAnim pNodeAnim)
    {
        for (int i = 0 ; i < pNodeAnim.mNumPositionKeys() - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.mPositionKeys().get(i + 1).mTime()) {
                return i;
            }
        }

        return 0;
    }


    int FindRotation(float AnimationTime, AINodeAnim pNodeAnim)
    {
        assert(pNodeAnim.mNumRotationKeys() > 0);

        for (int i = 0 ; i < pNodeAnim.mNumRotationKeys() - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.mRotationKeys().get(i + 1).mTime()) {
                return i;
            }
        }

        return 0;
    }


    int FindScaling(float AnimationTime, AINodeAnim pNodeAnim)
    {
        assert(pNodeAnim.mNumScalingKeys() > 0);

        for (int i = 0 ; i < pNodeAnim.mNumScalingKeys() - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.mScalingKeys().get(i + 1).mTime()) {
                return i;
            }
        }

        return 0;
    }

    protected void ReadNodeHeirarchy(float AnimationTime, AINode pNode, Matrix4f ParentTransform)
    {
        String NodeName = pNode.mName().dataString();

//        engine.animation pAnimation = null;//m_pScene.mAnimations[0];

        Matrix4f NodeTransformation = Maths.fromAssimpMatrix(pNode.mTransformation());//(pNode.mTransformation);

        AINodeAnim pNodeAnim = FindNodeAnim(animation, NodeName);

        if (pNodeAnim != null)
        {
            // Interpolate scaling and generate scaling transformation matrix
            Vector3f Scaling = new Vector3f(0, 0, 0);
            CalcInterpolatedScaling(Scaling, AnimationTime, pNodeAnim);
            Matrix4f ScalingM = new Matrix4f().scale(1,1,1); //.InitScale(Scaling.x(), Scaling.y(), Scaling.z());

            // Interpolate rotation and generate rotation transformation matrix
            Quaternion RotationQ = new Quaternion(0, 0, 0, 0);
            CalcInterpolatedRotation(RotationQ, AnimationTime, pNodeAnim);
            Matrix4f RotationM = Maths.toRotationMatrix(RotationQ);

            // Interpolate translation and generate translation transformation matrix
            Vector3f Translation = new Vector3f(0, 0, 0);
            CalcInterpolatedPosition(Translation, AnimationTime, pNodeAnim);
            Matrix4f TranslationM = new Matrix4f().translation(0,0,0); //InitTranslation(Translation.x(), Translation.y(), Translation.z());

            // Combine the above transformations
            NodeTransformation = TranslationM.mul(RotationM).mul(ScalingM);
        }

        Matrix4f GlobalTransformation = ParentTransform.mul(NodeTransformation);

        Bone bone = null;

        if ((bone = findBone(NodeName)) != null)
        {
            bone.setAnimationTransform(globalInverseTransform.mul(GlobalTransformation).mul(bone.localBindTransform));
        }

        for (int i = 0 ; i < pNode.mNumChildren(); i++) {
            ReadNodeHeirarchy(AnimationTime, AINode.create(pNode.mChildren().get(i)), GlobalTransformation);
        }
    }

    private final Bone findBone(String name)
    {
        for(Bone bone : bones) if(bone.name.equals(name)) return bone;

        return null;
    }

    public void boneTransforms(float timeInSeconds)
    {
        Matrix4f Identity = new Matrix4f();//.InitIdentity();

        float TicksPerSecond = (float)(animation.mTicksPerSecond() != 0 ? animation.mTicksPerSecond() : 25.0f);
        float TimeInTicks = timeInSeconds * TicksPerSecond;
        float AnimationTime = (TimeInTicks % (float)animation.mDuration());

        ReadNodeHeirarchy(AnimationTime, root, Identity);

//        boneTransforms.resize(m_NumBones);

        for (short i = 0 ; i < bones.length ; i++) {
            boneTransforms[i] = bones[i].getAnimationTransform();
        }
    }
}
