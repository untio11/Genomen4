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

        World.initWorld(MapConfigurations.getStarterMap());
        AbstractGameContainer gc = new GameContainerSwing(World.getInstance(), true);

        boolean fatherAI = true;
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
                File f = new File("res/network/father/1560138928134-single-genomen-1-8986.net");
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
            AIGenomenPlayer kidnapperController = new AIGenomenPlayer();
            if (!kidnapperLoad) {
                kidnapperController.init();
            } else {
                File f = new File("res/network/kidnapper/1560171989699-single-genomen-kidnapper-1-4972.net");
                try {
                    kidnapperController.loadNetwork(f);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            kidnapperController.setPlayer(World.getInstance().getKidnapper());
            gc.setKidnapperAI(kidnapperController);
        }

        gc.start();
        System.out.println(gc.isFatherWin() + " " + gc.getRemainingTime());
    }
}

