package Toolbox;

import GameState.Entities.Camera;
import org.apache.commons.math3.complex.Quaternion;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.assimp.AIMatrix4x4;
import org.lwjgl.assimp.AIQuaternion;
import org.lwjgl.assimp.AIVector3D;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, Vector3f scale, float rx, float ry) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        //matrix.rotateX((float)Math.toRadians(rx), matrix);
        matrix.rotateZ((float)Math.toRadians(ry), matrix);
        matrix.translate(translation);
        matrix.scale(new Vector3f(scale.x, scale.y, 1f));
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry,
                                                      float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation);
        matrix.rotateX((float)Math.toRadians(rx), matrix);
        matrix.rotateY((float)Math.toRadians(ry), matrix);
        matrix.rotateZ((float)Math.toRadians(rz), matrix);
        matrix.scale(new Vector3f(scale, scale, scale), matrix);
        return matrix;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotateX((float) Math.toRadians(camera.getPitch()), viewMatrix);
        viewMatrix.rotateY((float) Math.toRadians(camera.getYaw()), viewMatrix);
        viewMatrix.rotateZ((float) Math.toRadians(camera.getRoll()), viewMatrix);
        Vector3f cameraPos = camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        viewMatrix.translate(negativeCameraPos);
        return viewMatrix;
    }

    /**
     * Converts an Assimp Matrix4x4 into a Matrix4f
     * @param assimpMatrix
     * @return
     */
    public static Matrix4f fromAssimpMatrix(AIMatrix4x4 assimpMatrix) {
        Matrix4f m = new Matrix4f();
        m._m00(assimpMatrix.a1()); m._m01(assimpMatrix.a2());m._m02(assimpMatrix.a3());m._m03(assimpMatrix.a4());
        m._m10(assimpMatrix.b1()); m._m11(assimpMatrix.b2());m._m12(assimpMatrix.b3());m._m13(assimpMatrix.b4());
        m._m20(assimpMatrix.c1()); m._m21(assimpMatrix.c2());m._m22(assimpMatrix.c3());m._m23(assimpMatrix.c4());
        m._m30(assimpMatrix.d1()); m._m31(assimpMatrix.d2());m._m32(assimpMatrix.d3());m._m33(assimpMatrix.d4());
        return m;
    }

    /**
     * Converts an Assimp Vector3D into a Vector3f
     * @param assimpVec
     * @return
     */
    public static Vector3f fromAssimpVector(AIVector3D assimpVec) {
        return new Vector3f(assimpVec.x(), assimpVec.y(), assimpVec.z());
    }

    public static Quaternion fromAssimpQuat(AIQuaternion assimpQuat) {
        return new Quaternion(assimpQuat.x(), assimpQuat.y(), assimpQuat.z(), assimpQuat.w());
    }

    /**
     * Generates a Rotation Matrix from a Quaternion rotation
     * @param quaternion
     * @return
     */
    public static Matrix4f toRotationMatrix(Quaternion quaternion) {
        Matrix4f matrix = new Matrix4f();
        final float xy = (float)quaternion.getQ0() * (float)quaternion.getQ1();     //x * y
        final float xz = (float)quaternion.getQ0() * (float)quaternion.getQ2();     //x * z
        final float xw = (float)quaternion.getQ0() * (float)quaternion.getQ3();     //x * w
        final float yz = (float)quaternion.getQ1() * (float)quaternion.getQ2();     //y * z
        final float yw = (float)quaternion.getQ1() * (float)quaternion.getQ3();     //y * w
        final float zw = (float)quaternion.getQ2() * (float)quaternion.getQ3();     //z * w
        final float xSquared = (float)quaternion.getQ0() * (float)quaternion.getQ0();   //x * x
        final float ySquared = (float)quaternion.getQ1() * (float)quaternion.getQ1();   //y * y
        final float zSquared = (float)quaternion.getQ2() * (float)quaternion.getQ2();   //z * z
        matrix._m00( 1 - 2 * (ySquared + zSquared));
        matrix._m01( 2 * (xy - zw));
        matrix._m02( 2 * (xz + yw));
        matrix._m03( 0);
        matrix._m10( 2 * (xy + zw));
        matrix._m11( 1 - 2 * (xSquared + zSquared));
        matrix._m12( 2 * (yz - xw));
        matrix._m13( 0);
        matrix._m20( 2 * (xz - yw));
        matrix._m21( 2 * (yz + xw));
        matrix._m22( 1 - 2 * (xSquared + ySquared));
        matrix._m23( 0);
        matrix._m30( 0);
        matrix._m31( 0);
        matrix._m32( 0);
        matrix._m33( 1);
        return matrix;
    }

    /**
     * Interpolates between two quaternion rotations and returns the resulting
     * quaternion rotation. The interpolation method here is "nlerp", or
     * "normalized-lerp". Another mnethod that could be used is "slerp", and you
     * can see a comparison of the methods here:
     * https://keithmaggio.wordpress.com/2011/02/15/math-magician-lerp-slerp-and-nlerp/
     *
     * and here:
     * http://number-none.com/product/Understanding%20Slerp,%20Then%20Not%20Using%20It/
     *
     * @param aQuat
     * @param bQuat
     * @param blend
     *            - a value between 0 and 1 indicating how far to interpolate
     *            between the two quaternions.
     * @return The resulting interpolated rotation in quaternion format.
     */
    public static Quaternion interpolate(Quaternion aQuat, Quaternion bQuat, float blend) {
        Vector4f a = new Vector4f((float)aQuat.getQ0(), (float)aQuat.getQ1(), (float)aQuat.getQ2(), (float)aQuat.getQ3());
        Vector4f b = new Vector4f((float)bQuat.getQ0(), (float)bQuat.getQ1(), (float)bQuat.getQ2(), (float)bQuat.getQ3());

        Vector4f result = new Vector4f(0, 0, 0, 1);
        float dot = a.w * b.w + a.x * b.x + a.y * b.y + a.z * b.z;
        float blendI = 1f - blend;
        if (dot < 0) {
            result.w = blendI * a.w + blend * -b.w;
            result.x = blendI * a.x + blend * -b.x;
            result.y = blendI * a.y + blend * -b.y;
            result.z = blendI * a.z + blend * -b.z;
        } else {
            result.w = blendI * a.w + blend * b.w;
            result.x = blendI * a.x + blend * b.x;
            result.y = blendI * a.y + blend * b.y;
            result.z = blendI * a.z + blend * b.z;
        }
        Quaternion resultQuat = new Quaternion(result.x, result.y, result.z, result.w);
        resultQuat.normalize();
        return resultQuat;
    }

}
