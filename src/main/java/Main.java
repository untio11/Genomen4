import GameState.MapConfigurations;
import GameState.World;
import Graphics.WindowManager;

public class Main {
    public static void main(String[] args) {
        World.initWorld(MapConfigurations.getStarterMap());
        WindowManager wm = new WindowManager();
        wm.start();
    }
}

