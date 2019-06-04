package GameState;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorldTest {
    static World world;
    static int width = 60;
    static int height = 60;

    @BeforeClass
    public static void createWorld() {
        World.initWorld();
        world = World.getInstance();
    }

    @Test
    public void testInit() {
        Tile[][] data = world.getTiles();
        for (Tile[] row : data) {
            for (Tile cell : row) {
                assert (cell == null);
            }
        }
    }

    @Test
    public void testDimensions() {
        Assert.assertEquals(world.getTiles().length, height);
        Assert.assertEquals(world.getTiles()[0].length, width);
    }
}
