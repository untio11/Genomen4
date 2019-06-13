package Graphics.Animation;

import java.util.Map;

/**
 * Contains a Pose-map at a certain timeStamp
 */
public class KeyFrame {

    /*
    Pose map: Bone name - JointTransform
     */
    private final float timeStamp;
    private final Map<String, JointTransform> pose;

    /**
     * @param timeStamp
     *            - the time (in seconds) that this keyframe occurs during the
     *            animation.
     * @param jointKeyFrames
     *            - the local-space transforms for all the joints at this
     *            keyframe, indexed by the name of the joint that they should be
     *            applied to.
     */
    public KeyFrame(float timeStamp, Map<String, JointTransform> jointKeyFrames) {
        this.timeStamp = timeStamp;
        this.pose = jointKeyFrames;
    }

    /**
     * @return The time in seconds of the keyframe in the animation.
     */
    protected float getTimeStamp() {
        return timeStamp;
    }

    /**
     * @return The desired bone-space transforms of all the joints at this
     *         keyframe, of the animation, indexed by the name of the joint that
     *         they correspond to. This basically represents the "pose" at this
     *         keyframe.
     */
    protected Map<String, JointTransform> getJointKeyFrames() {
        return pose;
    }
}
