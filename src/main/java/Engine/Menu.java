package Engine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Menu implements KeyListener {
    private boolean running = false;

    private Window window;
    private MenuRenderer menuRenderer;
    boolean render;
    boolean playerFather = false;
    boolean raytracing = false;

    public Menu() {
        window = new Window(200, 200, 3);
        window.getFrame().addKeyListener(this);
        menuRenderer = new MenuRenderer(window);
    }

    public void run() {
        running = true;
        render = true;
        while (running) {
            if (render) {
                menuRenderer.clear();

                menuRenderer.drawText("Genomen 4", 80, 10, 0xffffffff);

                if (playerFather) {
                    menuRenderer.drawRect(30,40,40,8, 0xffff0000);
                } else {
                    menuRenderer.drawRect(130,40,40,8, 0xffff0000);
                }
                menuRenderer.drawText("Father", 38, 40, 0xffffffff);
                menuRenderer.drawText("Kidnapper", 130, 40, 0xffffffff);
                menuRenderer.drawText("Role: Press f or k", 65, 30, 0xffffffff);

                if (raytracing) {
                    menuRenderer.drawRect(30,70,40,8, 0xffff0000);
                } else {
                    menuRenderer.drawRect(130,70,40,8, 0xffff0000);
                }
                menuRenderer.drawText("Yes", 42, 70, 0xffffffff);
                menuRenderer.drawText("No", 145, 70, 0xffffffff);
                menuRenderer.drawText("Ray tracing: Press y or n", 50, 60, 0xffffffff);

                menuRenderer.drawText("Press Enter to start", 60, 180, 0xffffffff);

                window.update();
                render = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        window.close();
    }

    public boolean isPlayerFather() {
        return playerFather;
    }

    public boolean isRaytracing() {
        return raytracing;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_F) {
            playerFather = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_K) {
            playerFather = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_Y) {
            raytracing = true;
        }
        if (e.getKeyCode() == KeyEvent.VK_N) {
            raytracing = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            running = false;
        }
        render = true;
    }
}