package Graphics;

import GameState.Entities.Camera;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class Inputhandler {
    Camera camera;

    Inputhandler() {
        //camera = new Camera();
    }

    void handleInput(Camera camera, Set<Integer> pressedKeys) {
        for (int keyPressed : pressedKeys) {
            switch (keyPressed) {
                case GLFW_KEY_W:
                    camera.moveUp();
                    break;
                case GLFW_KEY_S:
                    camera.moveDown();
                    break;
                case GLFW_KEY_A:
                    camera.moveLeft();
                    break;
                case GLFW_KEY_D:
                    camera.moveRight();
                    break;
                case GLFW_KEY_UP:
                    camera.increaseRotation(-0.5f, 0, 0);
                    break;
                case GLFW_KEY_DOWN:
                    camera.increaseRotation(0.5f, 0, 0);
                    break;
                case GLFW_KEY_LEFT:
                    camera.increaseRotation(0, 0, -0.5f);
                    break;
                case GLFW_KEY_RIGHT:
                    camera.increaseRotation(0, 0, 0.5f);
                    break;
            }
        }
    }

}
