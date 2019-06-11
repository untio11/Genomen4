package Graphics.Animation.loaders;

import Graphics.Animation.*;
import Graphics.Models.BaseModel;
import Graphics.RenderEngine.Loader;
import Toolbox.Maths;
import org.apache.commons.math3.complex.Quaternion;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.assimp.*;

import java.util.HashMap;
import java.util.Map;

public class AnimModelLoader {
    private static int boneIndex;
    private static Bone[] bones;

    public static BaseModel loadAnimModelInVao(String fileName, Loader loader) {

        // import from the scene
        AIScene scene = Assimp.aiImportFile(fileName,
            Assimp.aiProcess_Triangulate |
                    Assimp.aiProcess_GenSmoothNormals |
                    Assimp.aiProcess_FlipUVs |
                    Assimp.aiProcess_CalcTangentSpace |
                    Assimp.aiProcess_LimitBoneWeights
        );

        if (scene == null | scene.mNumAnimations()==0) {
            System.err.println("The imported file does not contain any animations");
        }

        // get mesh from scene
        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));

        System.out.println("Num of vertices:"+mesh.mNumVertices());
        /**
         * position     3f
         * normal       3f
         * texCoord     2f
         * bone         4f
         * weight       4f
         */

        float[] verticesArray = new float[mesh.mNumVertices()*3];
        float[] textureArray = new float[mesh.mNumVertices()*2];
        float[] normalsArray = new float[mesh.mNumVertices()*3];
        int[] indicesArray = new int[mesh.mNumFaces() * mesh.mFaces().get(0).mNumIndices()];
        int[] boneArray = new int[mesh.mNumVertices() * 4];
        float[] boneWeight = new float[mesh.mNumVertices() * 4];

        int index3f = 0;
        int index2f = 0;

        /**
         * for every vertex, add its position, normal and texCoords to their array
         */
        for(int v=0; v<mesh.mNumVertices(); v++) {
            AIVector3D position = mesh.mVertices().get(v);
            AIVector3D normal = mesh.mNormals().get(v);
            AIVector3D texCoord = mesh.mTextureCoords(0).get(v);

            verticesArray[index3f] = position.x();
            verticesArray[index3f+1] = position.y();
            verticesArray[index3f+2] = position.z();

            normalsArray[index3f] = normal.x();
            normalsArray[index3f+1] = normal.y();
            normalsArray[index3f+2] = normal.z();

            textureArray[index2f] = texCoord.x();
            textureArray[index2f+1] = texCoord.y();

            index2f += 2;
            index3f += 3;
        }

        /**
         * fills indices array
         * for every face, loop its indices and store in array
         */
        int indCount = 0;
        for(int f=0; f < mesh.mNumFaces(); f++) {
            AIFace face = mesh.mFaces().get(f);
            for(int i=0; i < face.mNumIndices(); i++) {
                indicesArray[indCount] = face.mIndices().get(i);
                indCount++;
            }
        }

        /**
         * fills bone and weight array
         * loops every bone, then loops every weight
         */
        for(int b=0; b < mesh.mNumBones(); b++) {
            AIBone bone = AIBone.create(mesh.mBones().get(b));
            for(int w=0; w < bone.mNumWeights(); w++) {
                AIVertexWeight weight = bone.mWeights().get(w);
                int vertexIndex = weight.mVertexId() * 4;   // 4f bones per vertex
                /* For every weight of bone, store in array at corresponding vertex
                Check if there isn't already a bone stored for that vertex, and store in first empty spot
                The weight will be 0 without a bone
                 */
                if(boneWeight[vertexIndex] == 0) {
                    boneArray[vertexIndex] = b;
                    boneWeight[vertexIndex] = weight.mWeight();
                } else if(boneWeight[vertexIndex+1] == 0) {
                    boneArray[vertexIndex+1] = b;
                    boneWeight[vertexIndex+1] = weight.mWeight();
                } else if(boneWeight[vertexIndex+2] == 0) {
                    boneArray[vertexIndex+2] = b;
                    boneWeight[vertexIndex+2] = weight.mWeight();
                } else if(boneWeight[vertexIndex+3] == 0) {
                    boneArray[vertexIndex+3] = b;
                    boneWeight[vertexIndex+3] = weight.mWeight();
                } else {
                    System.err.println("max of 4 bones per vertex exceeded");
                }
            }
        }

        AIMatrix4x4 inverseRootTransform = scene.mRootNode().mTransformation();
        Matrix4f    inverseRootTransformation = Maths.fromAssimpMatrix(inverseRootTransform);

        /**
         * create Bones heirarchy and put them in a Bone array
         */
        bones = new Bone[mesh.mNumBones()];
        boneIndex = 0;
        AINode sceneNode = scene.mRootNode();
        AINode armatureNode = AINode.create(sceneNode.mChildren().get(0));
        AINode root = AINode.create(armatureNode.mChildren().get(0));
        Matrix4f rootTransform = Maths.fromAssimpMatrix(root.mTransformation());
        Bone rootBone = new Bone(root.mName().dataString(),boneIndex, null, rootTransform, rootTransform );
        bones[boneIndex] = rootBone;
        AINode[] childNodes = new AINode[root.mNumChildren()];
        for(int child=0; child < root.mNumChildren(); child++) {
            childNodes[child] = AINode.create(root.mChildren().get(child));
            setBoneHeirarchy( childNodes[child], rootBone);
        }


/**
 * Creating the KeyFrames from the animation
 */
        KeyFrame[] keyFrames = new KeyFrame[5];     // there should be 5 timeStamps
        AIAnimation aiAnimation = AIAnimation.create(scene.mAnimations().get(0));

        // creat list with nodeAnims
        AINodeAnim[] nodeAnims = new AINodeAnim[aiAnimation.mNumChannels()];
        for(int channel=0; channel<aiAnimation.mNumChannels(); channel++) {
            AINodeAnim nodeAnim= AINodeAnim.create(aiAnimation.mChannels().get(channel));
            nodeAnims[channel] = nodeAnim;
            // nodeAnim index not the same index as AINode and bones have but that is oke
        }

        /**
         * TimeStamps: 0 - 0.25 - 0.50 - 0.75 - 1
         */
        int timeIndex =0;
        for(double time=0; time <= 1; time+=0.25) {
            Map<String, JointTransform> poseMap = new HashMap<>();
            for(AINodeAnim nodeAnim : nodeAnims) {
                Vector3f position = new Vector3f();
                Quaternion rotation = new Quaternion(0,0,0,0);
                for(AIVectorKey vecKey : nodeAnim.mPositionKeys()) {
                    //System.out.print(vecKey.mTime()+", ");
                    if(vecKey.mTime() == time) {
                        position = Maths.fromAssimpVector(vecKey.mValue());
                    }
                }
                for(AIQuatKey quatKey : nodeAnim.mRotationKeys()) {
                    if (quatKey.mTime() == time) {
                        rotation = Maths.fromAssimpQuat(quatKey.mValue());
                    }
                }
                String boneName = nodeAnim.mNodeName().dataString();
                //System.out.println("jointTransform pos "+boneName+ ": "+position.x+" "+position.y+" "+position.z);
                JointTransform jointTransform = new JointTransform(position, rotation);
                poseMap.put(nodeAnim.mNodeName().dataString(), jointTransform);
            }
            KeyFrame keyFrame = new KeyFrame((float)time, poseMap);
            keyFrames[timeIndex] = keyFrame;
            timeIndex++;
        }

        /**
         * Initialize the Animation with the keyframes
         */
       // System.out.println("animDur:"+aiAnimation.mDuration());
       // System.out.println("ticks/sec: "+ aiAnimation.mTicksPerSecond());
        Animation animation = new Animation((float)aiAnimation.mDuration(), keyFrames);


        //TODO: Assimp.freeScene() somewhere to free scene from memory
        Assimp.aiFreeScene(scene);
        return loader.loadToModel(verticesArray, textureArray, normalsArray, indicesArray, boneArray, boneWeight, rootBone, mesh.mNumBones(), animation);
    }

    private static void setBoneHeirarchy(AINode node, Bone pBone) {
        String name = node.mName().dataString();
        Matrix4f localTransform = Maths.fromAssimpMatrix(node.mTransformation());
        boneIndex++;
        Bone bone = new Bone(name, boneIndex, pBone, localTransform, pBone.getInverseBindTransform());
        bones[boneIndex] = bone;
        pBone.addChild(bone);

        AINode[] childNodes = new AINode[node.mNumChildren()];
        for(int child=0; child < node.mNumChildren(); child++) {
            childNodes[child] = AINode.create(node.mChildren().get(child));
            setBoneHeirarchy( childNodes[child], bone);
        }
    }

}
