package AI.Genomen.Player;

import Engine.Controller.AIController;

import java.util.Random;

public class SimpleGenomenPlayer extends AIController {
    // The number of frames between every update of the ai player
    public static final int UPDATE_FREQUENCY = 30;

    // The maximum length of each ray coming from the player
    public static final int MAX_RAY_LENGTH = 6;

    // The maximum length of each ray coming from the player
    public final int angleOffset = 180;

    // The current number of frames elapsed since the last update
    protected int frame = 0;

    // Boolean indicating whether the player is the father
    protected boolean father;

    public SimpleGenomenPlayer() {
        this.father = false;
    }

    public SimpleGenomenPlayer(boolean father) {
        this.father = father;
    }

    @Override
    public void update(double dt) {

        if (frame < 0) {
            double[][] input = this.getInput(0, MAX_RAY_LENGTH);
            double angle = input [0][2];
            double xAxis;
            double yAxis;
            if (angle < 0) {
                Random r = new Random();
                xAxis = r.nextDouble() * 2 - 1;
                yAxis = r.nextDouble() * 2 - 1;
                frame = UPDATE_FREQUENCY;
                this.setBoost(false);
            } else {
                xAxis = Math.cos(Math.toRadians(angle + getAngleOffset()));
                yAxis = Math.sin(Math.toRadians(angle + getAngleOffset()));
                frame = UPDATE_FREQUENCY * getVisibleTimeoutFactor();
                if (input[0][0] > 0) {
                    this.setBoost(true);
                }
            }
            this.setAxis(xAxis, yAxis);
        }

        // Next, update the AI controller
        super.update(dt);

        // Decrease the frame counter
        frame--;
    }

    private double getAngleOffset() {
        if (this.father) {
            return 0;
        }
        return 180;
    }

    private int getVisibleTimeoutFactor() {
        if (this.father) {
            return 8;
        }
        return 2;
    }
}
