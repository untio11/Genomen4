import GameState.World;
import Graphics.WindowManager;

public class Main {
    public static void main(String[] args) {
        World.initWorld();
        WindowManager wm = new WindowManager();
        wm.start();
    }
}

