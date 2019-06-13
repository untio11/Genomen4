package Engine.Controller;

import GameState.World;
import org.joml.Vector3f;

public class AIController extends Controller {

    private double yAxis = 0;
    private double xAxis = 0;

    private boolean boost = false;

    @Override
    public void update(double dt) {
        player.setBoost(boost);
        player.move(xAxis * dt,-yAxis * dt, dt);
    }

    public void setAxis(double xAxis, double yAxis) {
        this.xAxis = Math.max(-1, Math.min(1, xAxis));
        this.yAxis = Math.max(-1, Math.min(1, yAxis));
    }

    public double getXAxis() {
        return xAxis;
    }

    public double getYAxis() {
        return yAxis;
    }

    public void setBoost(boolean value) {
        this.boost = value;
    }

    public boolean isBoost() {
        return boost;
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

    public double[] getOpponentPosition() {
        Vector3f pos = this.player.getOpponentPosition();
        World w = World.getInstance();
        double x = pos.x / w.getWidth();
        double y = pos.y / w.getHeight();
        x = x * 2 - 1;
        y = y * 2 - 1;
        return new double[] {x, y};
    }
}
