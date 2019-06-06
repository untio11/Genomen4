package Graphics.RenderEngine;

import org.joml.Matrix4f;


public class Bone {

    private String name;
    private Matrix4f offsetMatrix;

    public Bone(String name, Matrix4f offsetMatrix) {
        this.name = name;
        this.offsetMatrix = offsetMatrix;
    }
}
