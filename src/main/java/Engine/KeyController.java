package Engine;

import GameState.Player;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyController implements KeyListener {

    private final int NUM_KEYS = 256;
    private boolean[] keys = new boolean[NUM_KEYS];
    private Player player;
    private int up = KeyEvent.VK_W;
    private int down = KeyEvent.VK_S;
    private int left = KeyEvent.VK_A;
    private int right = KeyEvent.VK_D;
    private int rKey = KeyEvent.VK_R;

    public KeyController(Window window, Player player) {
        window.getCanvas().addKeyListener(this);
        this.player = player;
    }

    public void setKeys(int up, int down, int left, int right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public void update(double dt) {
        if (keys[right]) {
            player.moveRight(dt);
        }

        if (keys[left]) {
            player.moveLeft(dt);
        }

        if (keys[up]) {
            player.moveUp(dt);
        }

        if (keys[down]) {
            player.moveDown(dt);
        }

        if (keys[rKey]) {
            if (!player.isKidnapper()) {
                player.castRays(1);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) { keys[e.getKeyCode()] = true; }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
    }
}

