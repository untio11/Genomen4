package Engine;

import GameState.World;

import java.awt.event.KeyEvent;

public class GameContainer implements Runnable {

    private final double UPDATE_CAP = 1.0 / 60.0;
    private boolean running = false;
    private Thread thread = new Thread(this);

    private int width, height;
    private float scale = 1;

    private Window window;
    private Renderer renderer;
    private World world;
    private KeyController c1, c2;

    public GameContainer(World world) {
        this.world = world;
        width = World.TS * (world.getTileW());
        height = World.TS * (world.getTileH());
    }

    public void start() {
        window = new Window(width, height, scale);
        renderer = new Renderer(window, world);
        c1 = new KeyController(window, world.getKidnapper());
        c2 = new KeyController(window, world.getFather());
        c2.setKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        thread.run();
    }

    public void run() {
        running = true;

        boolean render;

        double firstTime;
        double lastTime = System.nanoTime() / 1e9d;
        double passedTime;
        double unprocessedTime = 0;

        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        while (running) {
            render = false;

            firstTime = System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {

                unprocessedTime -= UPDATE_CAP;
                render = true;

                c1.update(UPDATE_CAP);
                c2.update(UPDATE_CAP);

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            if (render) {
                renderer.clear();
                renderer.render();
                window.update();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        World world = World.getInstance();
        GameContainer gc = new GameContainer(world);
        gc.start();
    }
}