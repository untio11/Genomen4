package Graphics;

import Engine.Controller.Controller;
import GameState.Entities.Actor;
import GameState.Entities.Camera;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler extends Controller {

    private Actor player;
    Set<Integer> pressedKeys;

    public InputHandler(Actor player) {
        this.player = player;
        this.pressedKeys = new HashSet<>();
    }

    @Override
    public void passInput(Set<Integer> pressedKeys) {
        this.pressedKeys = pressedKeys;
    }

    @Override
    public void update(double dt) {
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
