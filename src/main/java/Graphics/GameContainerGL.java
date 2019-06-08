package Graphics;

import Engine.AbstractGameContainer;
import Engine.Controller.Controller;
import Engine.SoundClip;
import GameState.World;
import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.MasterRenderer;
import Graphics.RenderEngine.Scene;

import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class GameContainerGL implements Runnable, AbstractGameContainer {

    private static final double ROUND_TIME = 60;
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private final double cryInterval = 7;

    private boolean renderWindow;
    private int pixelWidth, pixelHeight;
    private float scale = 0.5f;
    private Scene scene;

    private double roundTime;
    private boolean fatherWin;

    private Thread thread = new Thread(this);

    private WindowGL windowGL;
    private AbstractRenderer renderer; // TODO: Add extra layer above MasterRenderer (AbstractRenderer?) to cover normal rasterizition shading and raytracing
    private Controller fatherController, kidnapperController;

    ArrayList<SoundClip> clips;
    SoundClip music;

    private World world;
    private int maxDistance;
    private boolean screamActive;

    public GameContainerGL(World world, boolean renderWindow) {
        this.world = world;
        int size = world.getMapConfig().getMapSize();
        int sizeSquared = size * size;
        this.maxDistance = (int) Math.sqrt(sizeSquared + sizeSquared);

        this.renderWindow = renderWindow;
        pixelWidth = 1600;
        pixelHeight = 900;
        if (renderWindow) {
            this.windowGL = new WindowGL(pixelWidth, pixelHeight, scale);
            renderer = new MasterRenderer();
            this.scene = new Scene(this.world); // First do window gl and initglfw, otherwise no openGL context will be available

            renderer.init(scene);

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
        this.kidnapperController = new InputHandler(World.getInstance().getKidnapper());
    }

    /**
     * Set father to player
     */
    public void setFatherPlayer() {
        this.fatherController = new InputHandler(World.getInstance().getFather());
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

    public void start() {
        if (kidnapperController != null || fatherController != null) {     //if all the controllers have been initialized
            thread.run();
        } else {
            System.err.println("Please define controllers");
        }
    }

    public void close() {
        renderer.clean();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowGL.getWindow());
        glfwDestroyWindow(windowGL.getWindow());

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void run() {
        if (renderWindow) {
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

    public void windowed() {
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
        while (!glfwWindowShouldClose(windowGL.getWindow()) || !running) { // TODO: Have a genaral Renderer.render() function to call
            render = false;
            firstTime = System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;
            roundTime -= passedTime;

            cryTimer -= passedTime;
            if (cryTimer < 0) {
                screamActive = true;
                startScreamTimer();
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
                    windowGL.close();
                }
                fatherWin = true;
                running = false;
                break;
            } else if (roundTime < 0) {
                if (this.renderWindow) {
                    windowGL.close();
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
        // render the given scene
        renderer.render(scene, screamActive);
        glfwSwapBuffers(windowGL.getWindow()); // swap the color buffers, that is: show on screen what is happening
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    public void updateActor() {
        fatherController.passInput(windowGL.getPressedKeys());
        fatherController.update(UPDATE_CAP);
        kidnapperController.passInput(windowGL.getPressedKeys());
        kidnapperController.update(UPDATE_CAP);
    }

    private void startScreamTimer() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = false;
                    }
                },
                2000
        );
    }

    public double getRemainingTime() { return roundTime; }

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