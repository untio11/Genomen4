package GameState;

import org.junit.Assert;
import org.junit.Test;

public class PositionTest {
    @Test
    public void testFloatyPos() {
        Position<Float> testpos = new Position<>(69.4f, 78.9f);
        Entity testent = new Entity();
        testent.setPosition(testpos);
        Assert.assertEquals(testent.getPosition().getX(), testpos.getX());
        Assert.assertEquals(testent.getPosition().getY(), testpos.getY());
    }
}
