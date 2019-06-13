import AI.Genomen.Player.GenomenAISettings;
import AI.Genomen.Player.LoadAIGenomenPlayer;
import Engine.AbstractGameContainer;
import Engine.Controller.Controller;
import Engine.GameContainerSwing;
import GameState.MapConfigurations;
import GameState.World;
import Graphics.GameContainerGL;

import java.io.File;

public class Main {
    public static void main(String[] args) {

        World.initWorld(MapConfigurations.getStarterMap());
        AbstractGameContainer gc = new GameContainerGL(World.getInstance(), true);

        // Booleans indicating whether to load the father or kidnapper AI or play as a human
        boolean fatherAI = false;
        boolean kidnapperAI = true;

        // If the father is the AI, load from the stored file and construct an AI player
        if (fatherAI) {
            File f = new File("res/network/father/1560138928134-single-genomen-1-8986.net");
            Controller fatherController = new LoadAIGenomenPlayer(f);

            /*
             * Example code for loading an AI with different settings
             * This AI was trained without ray inputs on an empty map, but performs reasonably on any map
             */
//            File f = new File("res/network/father/01-single-genomen-1-4092.net");
//            GenomenAISettings settings = new GenomenAISettings();
//            settings.setInputCount(0).setRememberCount(2);
//            Controller fatherController = new LoadAIGenomenPlayer(f, settings);

            fatherController.setPlayer(World.getInstance().getFather());
            gc.setFatherAI(fatherController);
        } else {
            // Else, set the father as the human player
            gc.setFatherPlayer();
        }

        // If the kidnapper is the AI, load from the stored file and construct an AI player
        if (kidnapperAI) {
            File f = new File("res/network/kidnapper/1560171989699-single-genomen-kidnapper-1-4972.net");
            Controller kidnapperController = new LoadAIGenomenPlayer(f);

            kidnapperController.setPlayer(World.getInstance().getKidnapper());
            gc.setKidnapperAI(kidnapperController);
        } else {
            // Else, set the kidnapper as the human player
            gc.setKidnapperPlayer();
        }

        gc.start();
        System.out.println(gc.isFatherWin() + " " + gc.getRemainingTime());
    }
}

