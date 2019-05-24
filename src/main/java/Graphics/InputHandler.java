package Graphics;

import Engine.Controller.Controller;
import GameState.Entities.Actor;
import GameState.Entities.Camera;

import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {

    private Actor player;

    public InputHandler(Actor player) {
        this.player = player;
    }

    void update(double dt, Set<Integer> pressedKeys) {
        for (int keyPressed : pressedKeys) {
            switch (keyPressed) {
                case GLFW_KEY_W:
                    player.moveUp(dt);
                    break;
                case GLFW_KEY_S:
                    player.moveDown(dt);
                    break;
                case GLFW_KEY_A:
                    player.moveLeft(dt);
                    break;
                case GLFW_KEY_D:
                    player.moveRight(dt);
                    break;
            }
        }
    }


}
