package Engine;

import GameState.Player;
import GameState.World;

import java.util.ResourceBundle;

public class GameContainer implements Runnable{

    private World world;
    private Window window;
    private Renderer renderer;

    private final double UPDATE_CAP = 1.0 / 60.0;
    private boolean running = false;
    private Thread thread = new Thread(this);

    public GameContainer(World world, Window window, Renderer renderer) {
        this.world = world;
        this.window = window;
        this.renderer = renderer;
    }

    public void start() {
        thread.run();
    }

    @Override
    public void run() {
        running = true;
        int i = 0;

        boolean render;

        double firstTime;
        double lastTime = System.nanoTime() / 1000000000.0;
        double passedTime;
        double unprocessedTime = 0;

        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        while (running) {
            render = false;

            firstTime = System.nanoTime() / 1000000000.0;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;

            unprocessedTime += passedTime;
            frameTime += passedTime;

            while (unprocessedTime >= UPDATE_CAP) {

                unprocessedTime -= UPDATE_CAP;
                render = true;

                //update input
                //update model

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    frames = 0;
                }
            }

            if (render) {

                //clear scene
                renderer.clear();
                //render scene
                renderer.drawRect(10,1 + i,16,16,0xff00ffff);
                i++;
                //display fps
                //display scene
                window.draw();
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

        World world = new World(5,4);
        world.createFather();
        world.createKidnapper();
        world.randomTiles();
        Window window = new Window(300,200,3);
        Renderer renderer = new Renderer(window);
        GameContainer gc = new GameContainer(world, window, renderer);
        gc.start();
    }
}
