package Graphics.Gui;

import org.joml.Vector3f;

public class GuiTexture {

    private int texture;
    private Vector3f position;
    private Vector3f scale;
    private float rx;
    private float ry;

    public GuiTexture(int texture, Vector3f position, Vector3f scale, float rx, float ry) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
        this.rx = rx;
        this.ry = ry;
    }

    public int getTexture() {
        return texture;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getScale() {
        return scale;
    }

    public float getRx() {
        return rx;
    }

    public void setRx(float rx) {
        this.rx = rx;
    }

    public float getRy() {
        return ry;
    }

    public void setRy(float ry) {
        this.ry = ry;
    }
}
