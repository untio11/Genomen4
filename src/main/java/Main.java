import AI.Genomen.Player.AIGenomenPlayer;
import AI.Genomen.Player.SimpleGenomenPlayer;
import Engine.AbstractGameContainer;
import Engine.GameContainerSwing;
import GameState.MapConfigurations;
import GameState.World;
import Graphics.GameContainerGL;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        /*World.initWorld(MapConfigurations.getStarterMap());
        AbstractGameContainer gc = new GameContainerGL(World.getInstance(), true);
        gc.setKidnapperPlayer();
        gc.setFatherPlayer();
        gc.start();
        System.out.println(wm.isFatherWin() + " " + wm.getRemainingTime());*/


        World.initWorld(MapConfigurations.getSimpleMap());
        AbstractGameContainer gc = new GameContainerGL(World.getInstance(), true);

        boolean fatherAI = false;
        boolean fatherLoad = true;


        boolean kidnapperAI = false;
        boolean kidnapperLoad = false;

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
                File f = new File("res/network/01-single-genomen-1-4092.net");
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
//                File f = new File("res/network/1559393144496-genomen-2-11711.net");
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

