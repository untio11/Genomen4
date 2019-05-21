package Engine.Controller;

import GameState.Entities.Actor;

public class AIController extends Controller {

    private double yAxis = 0;
    private double xAxis = 0;

    public AIController(Actor player) {
        super(player);
    }

    @Override
    public void update(double dt) {
        if (yAxis > 0) {
            player.moveUp(dt * yAxis);
        }
        if (yAxis < 0) {
            player.moveDown(dt * Math.abs(yAxis));
        }

        if (xAxis > 0) {
            player.moveRight(dt * yAxis);
        }
        if (xAxis < 0) {
            player.moveLeft(dt * Math.abs(yAxis));
        }
    }

    public void setAxis(double xAxis, double yAxis) {
        this.xAxis = Math.max(-1, Math.min(1, xAxis));
        this.yAxis = Math.max(-1, Math.min(1, yAxis));
    }
}
