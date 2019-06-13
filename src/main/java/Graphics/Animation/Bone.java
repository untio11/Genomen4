package Graphics.Animation;

import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;


public class Bone {

    public String name;
    public int index;

    private Bone parent;
    private List<Bone> children = new ArrayList<>();

    /* Animation from bind/rest pose to position in animated pose in model space
     * This get loaded up to the shader in uniform array, stored at index postition in that array
     */
    private Matrix4f animationTransform = new Matrix4f();

    /*
    used for calculations in the jointTransform
    localBindTransform: transformation in relation to parent bone in bind pose
    inverseBindTransform: inverse from transform in modelspace in bind pose
     */
    public Matrix4f localBindTransform;
    private Matrix4f inverseBindTransform;


//    private final Matrix4f offsetMatrix;    //relative to parent
//    private Matrix4f inverseBindTransform = new Matrix4f(); //Inverse transformation in model space
//    private Matrix4f finalTransformation = new Matrix4f();  //Final transformation from bind pose to animated pose


    public Bone(String name, int index, Bone parent, Matrix4f localTransform, Matrix4f parentWorldTransform ) {
        this.name = name;
        this.index = index;
        this.parent = parent;
        this.localBindTransform = localTransform;
        //this.inverseBindTransform = parentWorldTransform.mul(localTransform);
    }

    public void addChild(Bone child) {
        children.add(child);
    }

    public List<Bone> getChildren() {
        return children;
    }

    public Matrix4f getInverseBindTransform() {
        return inverseBindTransform;
    }

    // to set the current position of bone in animation
    public void setAnimationTransform(Matrix4f animationTransform) {
        this.animationTransform = animationTransform;
    }

    public Matrix4f getAnimationTransform() {
        return animationTransform;
    }

    /**
     * Called during set-up, after the joint heirarchy has been set
     * @param parentBindTransform
     */
    public void calcInverseBindTransform(Matrix4f parentBindTransform) {
        Matrix4f bindTransform = parentBindTransform.mul(localBindTransform);
        inverseBindTransform = bindTransform.invert();
        for (Bone childBone : children) {
            childBone.calcInverseBindTransform(bindTransform);
        }
    }

}
