package Engine.Controller;

import GameState.World;
import org.joml.Vector3f;

public class AIController extends Controller {

    private double yAxis = 0;
    private double xAxis = 0;

    @Override
    public void update(double dt) {
        player.move(xAxis,yAxis);
    }

    public void setAxis(double xAxis, double yAxis) {
        this.xAxis = Math.max(-1, Math.min(1, xAxis));
        this.yAxis = Math.max(-1, Math.min(1, yAxis));
    }

    public double[][] getInput(int nRays, int maxLength) {
        return this.player.castRays(nRays, maxLength);
    }

    public double[] getPosition() {
        Vector3f pos = this.player.getPosition();
        World w = World.getInstance();
        double x = pos.x / w.getWidth();
        double y = pos.y / w.getHeight();
        x = x * 2 - 1;
        y = y * 2 - 1;
        return new double[] {x, y};
    }
}
