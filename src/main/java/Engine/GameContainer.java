package Engine;

import AI.Genomen.Player.AIGenomenPlayer;
import AI.Genomen.Player.SimpleGenomenPlayer;
import Engine.Controller.AIController;
import Engine.Controller.Controller;
import Engine.Controller.KeyController;
import GameState.MapConfigurations;
import GameState.World;

import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;

public class GameContainer implements Runnable {
    
    private static final double ROUND_TIME = 60;
    private double cryInterval = 7;
    private final int FPS = 60;
    private final double UPDATE_CAP = 1.0 / FPS;
    private boolean running = false;
    private Thread thread = new Thread(this);
    private float speed = 1;
    private boolean renderWindow;
    private boolean humanPlayer = false;
    private double roundTime;
    private boolean fatherWin;
    double cryTimer;
    int cryNumber;

    private int pixelWidth, pixelHeight;
    private float scale = 0.5f;

    private Window window;
    private Renderer renderer;
    private World world;
    private Controller kidnapperController, fatherController;
    ArrayList<SoundClip> clips;
    SoundClip music;

    private int maxDistance;
    /**
     * @param renderWindow whether to render
     */
    public GameContainer(World world, boolean renderWindow) {
        music = new SoundClip("res/music.wav");
        clips = new ArrayList<SoundClip>();
        SoundClip clip1 = new SoundClip("res/cry1.wav");
        SoundClip clip2 = new SoundClip("res/cry2.wav");
        SoundClip clip3 = new SoundClip("res/cry3.wav");
        clips.add(clip1);
        clips.add(clip2);
        clips.add(clip3);
        this.world = world;
        int size = world.getMapConfig().getMapSize();
        int sizeSquared = size * size;
        this.maxDistance = (int) Math.sqrt(sizeSquared + sizeSquared);

        pixelWidth = Renderer.TS * (world.getWidth());
        pixelHeight = Renderer.TS * (world.getHeight());
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
     * Retrieve the father controller
     * @return The father controller
     */
    public Controller getFatherController() {
        return this.fatherController;
    }

    /**
     * Retrieve the kidnapper controller
     * @return The kidnapper controller
     */
    public Controller getKidnapperController() {
        return this.kidnapperController;
    }

    /**
     * Game Loop
     */
    public void run() {
        running = true;
        roundTime = ROUND_TIME;
        if (renderWindow) {
            windowed();
        } else {
            headless();
        }
    }

    public void scream(double passedTime) {
        cryTimer -= passedTime;
        if (cryTimer < 0) {
            World.getInstance().getKidnapper().receiveScream();
            World.getInstance().getFather().receiveScream();
            cryTimer = cryInterval;
            clips.get(cryNumber).play();
            cryNumber = (cryNumber + 1) % clips.size();
        }
    }

    public void headless() {
        double passedTime = 0;
        cryTimer = cryInterval;
        cryNumber = 0;

        while (running){
            kidnapperController.update(UPDATE_CAP);
            fatherController.update(UPDATE_CAP);
            roundTime -= UPDATE_CAP;
            passedTime += UPDATE_CAP;

            scream(passedTime);

            if (world.isPlayerCollision()) {
                fatherWin = true;
                running = false;
            } else if (roundTime < 0) {
                fatherWin = false;
                running = false;
            }
        }
    }


    public void windowed(){
        boolean render;
        double firstTime;
        double lastTime = speed * System.nanoTime() / 1e9d;
        double passedTime;
        double unprocessedTime = 0;
        double frameTime = 0;
        int frames = 0;
        int fps = 0;
        cryTimer = cryInterval;
        cryNumber = 0;

        window.display();
        music.loop();
        while (running) {
            render = false;
            firstTime = speed * System.nanoTime() / 1e9d;
            passedTime = firstTime - lastTime;
            lastTime = firstTime;
            unprocessedTime += passedTime;
            frameTime += passedTime;
            roundTime -= passedTime;

            scream(passedTime);

            //in case the game freezes, the while loop tries to catch up by updating faster
            while (unprocessedTime >= UPDATE_CAP) {

                unprocessedTime -= UPDATE_CAP;
                render = true;

                //update game
                kidnapperController.update(UPDATE_CAP);
                fatherController.update(UPDATE_CAP);

                if (frameTime >= 1.0) {
                    frameTime = 0;
                    fps = frames;
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
        music.stop();
    }

    public double getRemainingTime() {
        return roundTime;
    }

    public boolean isFatherWin() {
        return fatherWin;
    }

    public double getRoundTime() {
        return ROUND_TIME;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public static void main(String[] args) {
        World.initWorld(MapConfigurations.getBigEmptyMap());
        GameContainer gc = new GameContainer(World.getInstance(), true);

        boolean fatherAI = false;
        boolean fatherLoad = true;

        boolean kidnapperAI = true;
        boolean kidnapperLoad = true;

        if (!fatherAI) {
            gc.setFatherPlayer();
        }

        if (!kidnapperAI) {
            gc.setKidnapperPlayer();
        }

        if (fatherAI) {
            AIGenomenPlayer fatherController = new AIGenomenPlayer();
            if (!fatherLoad) {
                fatherController.init();
            } else {
                File f = new File("res/network/1558898711357-genomen-1-114.net");
                try {
                    fatherController.loadNetwork(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fatherController.setPlayer(World.getInstance().getFather());
            gc.setFatherAI(fatherController);
        }

        if (kidnapperAI) {
            SimpleGenomenPlayer kidnapperController = new SimpleGenomenPlayer();
//            if (!kidnapperLoad) {
//                kidnapperController.init();
//            } else {
//                File f = new File("res/network/1558898711357-genomen-2-6000.net");
//                try {
//                    kidnapperController.loadNetwork(f);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }

            kidnapperController.setPlayer(World.getInstance().getKidnapper());
            gc.setKidnapperAI(kidnapperController);
        }

        gc.start();
        System.out.println(gc.isFatherWin() + " " + gc.getRemainingTime());
    }

}