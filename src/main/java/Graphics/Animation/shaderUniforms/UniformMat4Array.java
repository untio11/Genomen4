package Graphics.Animation.shaderUniforms;

import org.joml.Matrix4f;

public class UniformMat4Array extends Uniform {

    private UniformMatrix[] matrixUniforms;

    public UniformMat4Array(String name, int size) {
        super(name);
        matrixUniforms = new UniformMatrix[size];
        for(int i=0;i<size;i++){
            matrixUniforms[i] = new UniformMatrix(name + "["+i+"]");
        }
    }

    @Override
    public void storeUniformLocation(int programID) {
        for(UniformMatrix matrixUniform : matrixUniforms){
            matrixUniform.storeUniformLocation(programID);
        }
    }

    public void loadMatrixArray(Matrix4f[] matrices){
        for(int i=0;i<matrices.length;i++){
            matrixUniforms[i].loadMatrix(matrices[i]);
        }
    }
}
