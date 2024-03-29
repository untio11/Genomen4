package Graphics.Animation.shaderUniforms;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public class UniformMatrix extends Uniform {

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public UniformMatrix(String name) {
        super(name);
    }

    public void loadMatrix(Matrix4f matrix){
        //matrix.store(matrixBuffer);
        //matrixBuffer.flip();
        GL20.glUniformMatrix4fv(super.getLocation(), false, matrix.get(matrixBuffer));
    }
}
