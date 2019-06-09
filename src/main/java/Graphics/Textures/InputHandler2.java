package Graphics.Textures;

import Engine.Controller.Controller;
import GameState.Entities.Actor;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler2 extends Controller {

    private Actor player;
    Set<Integer> pressedKeys;

    public InputHandler2(Actor player) {
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
        for (int keyPressed : pressedKeys) {
            switch (keyPressed) {
                case GLFW_KEY_UP:
                    up = true;
                    break;
                case GLFW_KEY_DOWN:
                    down = true;
                    break;
                case GLFW_KEY_LEFT:
                    left = true;
                    break;
                case GLFW_KEY_RIGHT:
                    right = true;
                    break;
            }
        }
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

        player.move(horizontal, vertical);
    }


}
