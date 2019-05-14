import GameState.World;
import Graphics.WindowManager;

public class Main {
    public static void main(String[] args) {
        World.initWorld(100, 100);
        new WindowManager().run();
    }
}