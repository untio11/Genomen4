package Engine.Controller;

import Engine.Window;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;


public class KeyController extends Controller implements KeyListener {

    private final int NUM_KEYS = 256;
    private boolean[] keys = new boolean[NUM_KEYS];

    private int up = KeyEvent.VK_W;
    private int down = KeyEvent.VK_S;
    private int left = KeyEvent.VK_A;
    private int right = KeyEvent.VK_D;


    public KeyController(Window window) {
        window.getCanvas().addKeyListener(this);
    }

    public void setKeys(int up, int down, int left, int right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    @Override
    public void update(double dt) {
        double horizontal = 0;
        double vertical = 0;

        if (keys[right] && !keys[left]) {
            horizontal = dt;
        }

        if (keys[left] && !keys[right]) {
            horizontal = -dt;
        }

        if (keys[up] && !keys[down]) {
            vertical = -dt;
        }

        if (keys[down] && !keys[up]) {
            vertical = dt;
        }

        player.move(horizontal, vertical);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = false;
        System.out.println(1);
    }
}

