package GameState;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.BeforeClass;

public class MapGeneratorTest {

    static MapGenerator mapGenerator;

    @BeforeClass
    public static void createWorld() {
        mapGenerator = new MapGenerator();
    }

    @Test
    public void testInit() {

    }

    @Test
    public void testGenerate() {
        mapGenerator.generate();
        System.out.println(mapGenerator.toString());
    }
}