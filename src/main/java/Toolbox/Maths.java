package Toolbox;

import GameState.Entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

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

}
