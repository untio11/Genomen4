package Graphics.RenderEngine;

import Graphics.Models.BaseModel;
import org.joml.Matrix4f;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class AnimModelLoader {

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
        float[] boneArray = new float[mesh.mNumVertices() * 4];
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
        Matrix4f    inverseRootTransformation = fromAssimpMatrix(inverseRootTransform);

        /**
         * create Bones and put them in a Bone array
         */
        Bone bones[] = new Bone[mesh.mNumBones()];
        for(int b=0;b < mesh.mNumBones(); b++) {
            AIBone bone = AIBone.create(mesh.mBones().get(b));
            String name = bone.mName().dataString();
            Matrix4f offsetMatrix = fromAssimpMatrix(bone.mOffsetMatrix());
            bones[b] = new Bone(name, offsetMatrix);
        }

        //TODO: Assimp.freeScene() somewhere to free scene from memory
        return loader.loadToModel(verticesArray, textureArray, normalsArray, indicesArray);
    }

    private static Matrix4f fromAssimpMatrix(AIMatrix4x4 AssimpMatrix) {
        Matrix4f m = new Matrix4f();
        m._m00(AssimpMatrix.a1()); m._m01(AssimpMatrix.a2());m._m02(AssimpMatrix.a3());m._m03(AssimpMatrix.a4());
        m._m10(AssimpMatrix.b1()); m._m11(AssimpMatrix.b2());m._m12(AssimpMatrix.b3());m._m13(AssimpMatrix.b4());
        m._m20(AssimpMatrix.c1()); m._m21(AssimpMatrix.c2());m._m22(AssimpMatrix.c3());m._m23(AssimpMatrix.c4());
        m._m30(AssimpMatrix.d1()); m._m31(AssimpMatrix.d2());m._m32(AssimpMatrix.d3());m._m33(AssimpMatrix.d4());
        return m;
    }


}
