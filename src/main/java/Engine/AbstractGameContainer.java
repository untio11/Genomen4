package Engine;

import Engine.Controller.Controller;
import GameState.World;
import Graphics.RenderEngine.AbstractRenderer;
import Graphics.RenderEngine.Scene;

import java.util.ArrayList;

public abstract class AbstractGameContainer implements Runnable{

    public static final double ROUND_TIME = 60;
    public final int FPS = 60;
     public final double UPDATE_CAP = 1.0 / FPS;
    public final double cryInterval = 7;

     public boolean renderWindow;
    public int pixelWidth, pixelHeight;
    public float scale = 0.5f;
    public Scene scene;

    public double roundTime;
    public boolean fatherWin;

    public Thread thread = new Thread(this);

    public ArrayList<SoundClip> clips;
    public SoundClip music;

    public World world;
    public int maxDistance;


    public Window window;
    public AbstractRenderer renderer; // TODO: Add extra layer above MasterRenderer (AbstractRenderer?) to cover normal rasterizition shading and raytracing
    public Controller fatherController, kidnapperController;

    public abstract void setKidnapperPlayer();

    public abstract void setFatherPlayer();

    public void setFatherAI(Controller c) {
        fatherController = c;
    }

    public void setKidnapperAI(Controller c) {
        kidnapperController = c;
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

    public abstract void finalRender();

    public abstract void close();

    public abstract void updateActor();

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
