package Engine;

import Engine.Controller.KeyController;
import GameState.World;

import java.awt.event.KeyEvent;

public class GameContainer implements Runnable {
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private boolean running = false;
    private Thread thread = new Thread(this);

    private int pixelWidth, pixelHeight;
    private float scale = 1;

    private Window window;
    private Renderer renderer;
    private World world;
    private KeyController c1, c2;

    public GameContainer(World world) {
        this.world = world;
        pixelWidth = Renderer.TS * (world.getWidth());
        pixelHeight = Renderer.TS * (world.getHeight());
    }


    /**
     * Initialise game and run.
     */
    public void start() {
        window = new Window(pixelWidth, pixelHeight, scale);
        renderer = new Renderer(window, world);
        //add keyboard controls to both player
        c1 = new KeyController(window, world.getKidnapper());
        c2 = new KeyController(window, world.getFather());
        c2.setKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        thread.run();
    }

    /**
     * Game Loop
     */
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

            //in case the game freezes, the while loop tries to catch up by updating faster
            while (unprocessedTime >= UPDATE_CAP) {

                unprocessedTime -= UPDATE_CAP;
                render = true;

                //update game
                c1.update(UPDATE_CAP);
                c2.update(UPDATE_CAP);

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            if (render) {
                renderer.clear();   //clear window
                renderer.render();  //render game
                window.update();    //draw window
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
        World.initWorld(50, 50);
        GameContainer gc = new GameContainer(World.getInstance());
        gc.start();
    }

}