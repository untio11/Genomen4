package Engine;

import GameState.World;

public class GameContainer implements Runnable{

    private World world;
    private Window window;

    private final double UPDATE_CAP = 1.0 / 60.0;
    private boolean running = false;
    private Thread thread = new Thread(this);

    public GameContainer(World world, Window window) {
        this.world = world;
        this.window = window;
    }

    public void start() {
        thread.run();
    }

    @Override
    public void run() {
        running = true;

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
                //render scene
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
        Window window = new Window(100,200,3);
        GameContainer gc = new GameContainer(world, window);
        gc.start();
    }
}
