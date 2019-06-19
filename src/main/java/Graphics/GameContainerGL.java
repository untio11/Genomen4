package Graphics;

import AI.Genomen.Player.*;
import Engine.AbstractGameContainer;
import Engine.Controller.Controller;
import Engine.Menu;
import Engine.SoundClip;
import GameState.World;
import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.RayTracing.RayTracer;
import Graphics.RenderEngine.TraditionalRendering.MasterRenderer;
import Graphics.RenderEngine.Scene;

import java.io.File;
import java.util.ArrayList;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

public class GameContainerGL implements AbstractGameContainer {
    public static boolean RAY_TRACING = false;
    private static final double ROUND_TIME = 60;
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private final double cryInterval = 3;
    private final double humanCryInterval = 7;

    private boolean renderWindow;
    private int pixelWidth, pixelHeight;
    private float scale = 0.5f;
    private Scene scene;

    private double roundTime;
    private boolean fatherWin;
    private static boolean playerFather;

    private WindowGL windowGL;
    private AbstractRenderer renderer;
    private Controller fatherController, kidnapperController;

    ArrayList<SoundClip> clips;
    SoundClip music;

    private World world;
    private int maxDistance;
    private boolean screamActive;
    private int oppoAngle;

    public GameContainerGL(World world, boolean renderWindow) {
        this.world = world;
        int size = world.getMapConfig().getMapSize();
        int sizeSquared = size * size;
        this.maxDistance = (int) Math.sqrt(sizeSquared + sizeSquared);

        this.renderWindow = renderWindow;
        pixelWidth = 1600;
        pixelHeight = 900;
    }

    public void init() {
        this.windowGL = new WindowGL(pixelWidth, pixelHeight, scale);
        renderer = RAY_TRACING ? new RayTracer(pixelWidth, pixelHeight) : new MasterRenderer();
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

    /**
     * Closes and cleans GL
     */
    public void close() {
        glfwSetWindowShouldClose(windowGL.getWindow(), true);
        renderer.clean();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(windowGL.getWindow());
        glfwDestroyWindow(windowGL.getWindow());

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    /**
     * run the game, either with graphics or headless
     */
    public void start() {
        if (renderWindow) {
            menu();
            init();
            windowed();
            end();
        } else {
            headless();
        }
    }

    /**
     * launch game menu
     */
    public void menu() {
        Menu menu = new Menu();
        menu.run();
        if (menu.isPlayerFather()) {
            setFatherPlayer();
            File f = new File("res/network/kidnapper/1560215259902-single-genomen-kidnapper-1-5809.net");
            GenomenAISettings settings = new GenomenAISettings();
            settings.setAddBoost(true);
            Controller kidnapperController = new LoadAIGenomenPlayer(f, settings);
            kidnapperController.setPlayer(World.getInstance().getKidnapper());
            setKidnapperAI(kidnapperController);
            world.setCameraFather();
            playerFather = true;
        } else {
            Controller fatherController = new CombinedAIGenomenPlayer();
            fatherController.setPlayer(World.getInstance().getFather());
            setFatherAI(fatherController);
            setKidnapperPlayer();
            world.setCameraKidnapper();
            playerFather = false;
        }
        RAY_TRACING = menu.isRaytracing();
    }

    /**
     * display end screen with result of game
     */
    public void end() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (!glfwWindowShouldClose(windowGL.getWindow())) {
            boolean win = false;
            if ((fatherWin && playerFather) || (!fatherWin && !playerFather))  {
                win = true;
            }
            //render end screen
            renderer.renderEnd(win);
            glfwSwapBuffers(windowGL.getWindow()); // swap the color buffers, that is: show on screen what is happening
            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            if (windowGL.getPressedKeys().size() > 0) {
                break;
            }
        }
        close();
    }

    /**
     * run game without graphics as faast as possible
     */
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
                break;
            } else if (roundTime < 0) {
                fatherWin = false;
                break;
            }
        }
        this.roundTime = roundTime;
    }

    /**
     * run game with graphics
     */
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
        double humanCryTimer = humanCryInterval;
        int cryNumber = 0;
        double roundTime= ROUND_TIME;

        music.loop();
        while (!glfwWindowShouldClose(windowGL.getWindow())) {
            render = false;
            firstTime = System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;
            roundTime -= passedTime;

            cryTimer -= passedTime;
            humanCryTimer -= passedTime;
            if (cryTimer < 0) {
                World.getInstance().getKidnapper().receiveScream();
                World.getInstance().getFather().receiveScream();
                if (playerFather) {
                    oppoAngle = (int) World.getInstance().getFather().getPreviousAngle();
                } else {
                    oppoAngle = (int) World.getInstance().getKidnapper().getPreviousAngle();
                }
                cryTimer = cryInterval;
            }

            if (humanCryTimer < 0) {
                screamActive = true;
                startScreamTimer();
                if (playerFather) {
                    World.getInstance().getFather().receiveScream();
                } else {
                    World.getInstance().getKidnapper().receiveScream();
                }
                if (playerFather) {
                    oppoAngle = (int) World.getInstance().getFather().getPreviousAngle();
                } else {
                    oppoAngle = (int) World.getInstance().getKidnapper().getPreviousAngle();
                }
                humanCryTimer = humanCryInterval;
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
                break;
            } else if (roundTime < 0) {
                if (this.renderWindow) {
                    windowGL.close();
                }
                fatherWin = false;
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
        this.roundTime = roundTime;
        music.stop();
    }

    /**
     * render game
     */
    public void finalRender() {
        // render the given scene
        renderer.render(scene, screamActive, oppoAngle);
        glfwSwapBuffers(windowGL.getWindow()); // swap the color buffers, that is: show on screen what is happening
        // Poll for window events. The key callback above will only be
        // invoked during this call.
        glfwPollEvents();
    }

    /**
     * update father and kidnapper from keyboard input
     */
    public void updateActor() {
        fatherController.passInput(windowGL.getPressedKeys());
        fatherController.update(UPDATE_CAP);
        kidnapperController.passInput(windowGL.getPressedKeys());
        kidnapperController.update(UPDATE_CAP);
    }

    /**
     * timer for scream indicator flicker
     */
    private void startScreamTimer() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = false;
                    }
                },
                500
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = true;
                    }
                },
                1000
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = false;
                    }
                },
                1500
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = true;
                    }
                },
                2000
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        screamActive = false;
                    }
                },
                2500
        );
    }

    /**
     * Set kidnapper to player
     */
    public void setKidnapperPlayer() {
        this.kidnapperController = new InputHandler(World.getInstance().getKidnapper()); }

    /**
     * Set father to player
     */
    public void setFatherPlayer() {
        this.fatherController = new InputHandler(World.getInstance().getFather()); }

    /**
     * Set father to AI
     */
    public void setFatherAI(Controller c) {
        fatherController = c; }

    /**
     * Set kidnapper to AI
     */
    public void setKidnapperAI(Controller c) {
        kidnapperController = c; }

    public double getRemainingTime() { return roundTime; }

    public boolean isFatherWin() { return fatherWin; }

    public int getMaxDistance() { return maxDistance; }

    public static double getRoundTime() { return ROUND_TIME; }

    public Controller getFatherController() { return fatherController; }

    public static boolean isPlayerFather() { return playerFather; }

    public Controller getKidnapperController() { return kidnapperController; }
}