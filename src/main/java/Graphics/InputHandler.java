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
        boolean right = false;
        boolean left = false;
        boolean up = false;
        boolean down = false;
        boolean shift = false;
        for (int keyPressed : pressedKeys) {
            switch (keyPressed) {
                case GLFW_KEY_W:
                    up = true;
                    break;
                case GLFW_KEY_S:
                    down = true;
                    break;
                case GLFW_KEY_A:
                    left = true;
                    break;
                case GLFW_KEY_D:
                    right = true;
                    break;
                case GLFW_KEY_LEFT_SHIFT:
                    shift = true;
                    break;
            }
        }

        player.setBoost(shift);

        double horizontal = 0;
        double vertical = 0;

        if (right && !left) {
            horizontal = dt;
        }

        if (left && !right) {
            horizontal = -dt;
        }

        if (up && !down) {
            vertical = -dt;
        }

        if (down && !up) {
            vertical = dt;
        }

        player.move(horizontal, vertical, dt);
    }


}
