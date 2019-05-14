package GameState;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class WorldTest {
    static World world;
    static int width = 42;
    static int height = 69;

    @BeforeClass
    public static void createWorld() {
        world = new World(width, height);
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
