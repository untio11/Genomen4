package Engine;

import Engine.Controller.Controller;
import Engine.Controller.KeyController;
import GameState.World;
import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.RenderInterface;
import Graphics.RenderEngine.Scene;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameContainerSwing implements Runnable, AbstractGameContainer {

    private static final double ROUND_TIME = 60;
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private final double cryInterval = 3;

    private boolean renderWindow;
    private int pixelWidth, pixelHeight;
    private float scale = 0.5f;
    private Scene scene;

    private double roundTime;
    private boolean fatherWin;

    private Thread thread = new Thread(this);

    private Window window;
    private RenderInterface renderer;
    private Controller fatherController, kidnapperController;

    ArrayList<SoundClip> clips;
    SoundClip music;

    private World world;
    private int maxDistance;

    private boolean humanPlayer = false;

    /**
     * @param renderWindow whether to render
     */
    public GameContainerSwing(World world, boolean renderWindow) {
        this.world = world;
        int size = world.getMapConfig().getMapSize();
        int sizeSquared = size * size;
        this.maxDistance = (int) Math.sqrt(sizeSquared + sizeSquared);

        this.renderWindow = renderWindow;
        pixelWidth = Renderer.TS * (world.getWidth());
        pixelHeight = Renderer.TS * (world.getHeight());
        if (renderWindow) {
            window = new Window(pixelWidth, pixelHeight, scale);
            renderer = new Renderer(window, world);
            music = new SoundClip("res/music.wav");
            clips = new ArrayList<>();

            SoundClip clip1 = new SoundClip("res/cry1.wav");
            SoundClip clip2 = new SoundClip("res/cry2.wav");
            SoundClip clip3 = new SoundClip("res/cry3.wav");
            clips.add(clip1);
            clips.add(clip2);
            clips.add(clip3);
        }
    }

    /**
     * Set kidnapper to player
     */
    public void setKidnapperPlayer() {
        if (window != null) {
            KeyController c = new KeyController(window);
            c.setPlayer(world.getKidnapper());
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
            KeyController c = new KeyController(window);
            c.setPlayer(world.getFather());
            changeKey2P(c);
            fatherController = c;
        } else {
            System.err.println("No window for player");
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
     * Initialise game and run.
     */
    public void start() {
        if (kidnapperController != null || fatherController != null) {     //if all the controllers have been initialized
            thread.run();
        } else {
            System.err.println("Please define controllers");
        }
    }

    public void close() {}

    /**
     * Game Loop
     */
    public void run() {
        if (renderWindow) {
            window.display();
            windowed();
        } else {
            headless();
        }
    }

    public void headless() {
        double passedTime = 0;
        double cryTimer = cryInterval;
        boolean running = true;
        double roundTime= ROUND_TIME;

        while (running){
            updateActor();
            roundTime -= UPDATE_CAP;
            passedTime += UPDATE_CAP;

            cryTimer -= passedTime;
            if (cryTimer < 0) {
                World.getInstance().getKidnapper().receiveScream();
                World.getInstance().getFather().receiveScream();
                cryTimer = cryInterval;
            }

            if (world.isPlayerCollision()) {
                fatherWin = true;
                running = false;
                break;
            } else if (roundTime < 0) {
                fatherWin = false;
                running = false;
                break;
            }
        }
        this.roundTime = roundTime;
    }


    public void windowed(){
        boolean render;
        double firstTime;
        double lastTime = System.nanoTime() / 1e9d;
        double passedTime;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;
        int fps = 0;
        double cryTimer = cryInterval;
        int cryNumber = 0;
        boolean running = true;
        double roundTime= ROUND_TIME;

        music.loop();
        while (running) {
            render = false;
            firstTime = System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;
            roundTime -= passedTime;
            
            cryTimer -= passedTime;
            if (cryTimer < 0) {
                World.getInstance().getKidnapper().receiveScream();
                World.getInstance().getFather().receiveScream();
                cryTimer = cryInterval;
                clips.get(cryNumber).play();
                cryNumber = (cryNumber + 1) % clips.size();
            }

            //in case the game freezes, the while loop tries to catch up by updating faster
            while (unprocessedTime >= UPDATE_CAP) {

                render = true;
                unprocessedTime -= UPDATE_CAP;

                //update game
                updateActor();

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
                    System.out.println(fps + "fps <=> " + (float) 1000/frames + "ms/frame");
                    frames = 0;
                }
            }

            if (world.isPlayerCollision()) {
                if (this.renderWindow) {
                    window.close();
                }
                fatherWin = true;
                running = false;
                break;
            } else if (roundTime < 0) {
                if (this.renderWindow) {
                    window.close();
                }
                fatherWin = false;
                running = false;
                break;
            }

            if (render) {
                finalRender();

                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        music.stop();
        close();
        this.roundTime = roundTime;
    }

    public void finalRender() {
        renderer.render(scene, false, 0);  //render game
    }

    public void updateActor() {
        fatherController.update(UPDATE_CAP);
        kidnapperController.update(UPDATE_CAP);
    }

    public double getRemainingTime() {
        return roundTime;
    }

    public boolean isFatherWin() {
        return fatherWin;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public static double getRoundTime() { return ROUND_TIME; }

    public Controller getFatherController() { return fatherController; }

    public Controller getKidnapperController() { return kidnapperController; }
}