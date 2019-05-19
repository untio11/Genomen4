package Engine;

import Engine.Controller.AIController;
import Engine.Controller.Controller;
import Engine.Controller.KeyController;
import GameState.World;

import java.awt.event.KeyEvent;

public class GameContainer implements Runnable {
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private boolean running = false;
    private Thread thread = new Thread(this);
    private float speed;
    private boolean renderWindow;
    private boolean humanPlayer = false;
    private double roundTime = 60;
    private boolean fatherWin;

    private int pixelWidth, pixelHeight;
    private float scale = 0.5f;

    private Window window;
    private Renderer renderer;
    private World world;
    private Controller kidnapperController, fatherController;

    /**
     * @param speed time speed multiplier
     * @param renderWindow whether to render
     */
    public GameContainer(World world, float speed, boolean renderWindow) {
        this.world = world;
        pixelWidth = Renderer.TS * (world.getWidth());
        pixelHeight = Renderer.TS * (world.getHeight());
        this.speed = speed;
        this.renderWindow = renderWindow;
        if (renderWindow) {
            window = new Window(pixelWidth, pixelHeight, scale);
            renderer = new Renderer(window, world);
        }
    }


    /**
     * Initialise game and run.
     */
    public void start() {
        if (renderWindow) {
            window.display();
        }
        if (kidnapperController != null || fatherController != null) {     //if all the controllers have been initialized
            thread.run();
        } else {
            System.err.println("Please define controllers");
        }
    }

    /**
     * Set kidnapper to player
     */
    public void setKidnapperPlayer() {
        if (window != null) {
            KeyController c = new KeyController(window, world.getKidnapper());
            changeKey2P(c);
            kidnapperController = c;
        } else {
            System.err.println("No window for player");
        }
    }

    /**
     * Set father to player
     */
    public void setFatherPlayer() {
        if (window != null) {
            KeyController c = new KeyController(window, world.getFather());
            changeKey2P(c);
            fatherController = c;
        } else {
            System.err.println("No window for player");
        }
    }

    /**
     * In case that there are two players, set different key layout for the 2nd player
     */
    public void changeKey2P(KeyController c) {
        if (humanPlayer) {
            c.setKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        } else {
            humanPlayer = true;
        }
    }

    /**
     * Set father to AI
     */
    public void setFatherAI(Controller c) {
        fatherController = c;
    }

    /**
     * Set kidnapper to AI
     */
    public void setKidnapperAI(Controller c) {
        kidnapperController = c;
    }

    /**
     * Game Loop
     */
    public void run() {
        running = true;

        boolean render;

        double firstTime;
        double lastTime = speed * System.nanoTime() / 1e9d;
        double passedTime;
        double unprocessedTime = 0;

        double frameTime = 0;
        int frames = 0;
        int fps = 0;

        while (running) {
            if (world.isPlayerCollision()) {
                window.close();
                fatherWin = true;
                break;
            } else if (roundTime < 0) {
                window.close();
                fatherWin = false;
                break;
            }

            render = false;
            firstTime = speed * System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;

            roundTime -= passedTime;

            //in case the game freezes, the while loop tries to catch up by updating faster
            while (unprocessedTime >= UPDATE_CAP) {

                unprocessedTime -= UPDATE_CAP;
                if (renderWindow) {
                    render = true;
                }

                //update game
                kidnapperController.update(UPDATE_CAP);
                fatherController.update(UPDATE_CAP);

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

    public double getRemainingTime() {
        return roundTime;
    }

    public boolean isFatherWin() {
        return fatherWin;
    }

    public static void main(String[] args) {
        World.initWorld(100, 100);
        GameContainer gc = new GameContainer(World.getInstance(), 4, true);
        gc.setFatherPlayer();
        //gc.setKidnapperPlayer();
        //gc.setFatherAI(new AIController(World.getInstance().getFather()));
        gc.setKidnapperAI(new AIController(World.getInstance().getKidnapper()));
        gc.start();
        System.out.println(gc.isFatherWin() + " " + gc.getRemainingTime());
    }

}