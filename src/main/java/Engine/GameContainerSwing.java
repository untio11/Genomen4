package Engine;

import Engine.Controller.Controller;
import Engine.Controller.KeyController;
import GameState.World;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class GameContainerSwing extends AbstractGameContainer {

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

    public void finalRender() {
        renderer.render(scene);  //render game
    }

    public void updateActor() {
        fatherController.update(UPDATE_CAP);
        kidnapperController.update(UPDATE_CAP);
    }


}