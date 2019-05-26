package Graphics.Animation;

import org.joml.Matrix4f;

public class Frame {

    private String frameID;         // frame name
    private Matrix4f transformationMatrix;  //transformation for animation
    private Frame[] childrem;       // array of child frames
    private Matrix4f toParent;      // transformation matrix from bone-space to bone's parent-space
    private Matrix4f toRoot;        // transformation matrix from bone-space to root-frame space

}
