import AI.Genomen.Player.*;
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
        gc.start();
        System.out.println(gc.isFatherWin() + " " + gc.getRemainingTime());
        System.exit(0);
    }
}

