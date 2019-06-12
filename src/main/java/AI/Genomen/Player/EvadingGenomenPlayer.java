package AI.Genomen.Player;

import Engine.Controller.AIController;

import java.util.Random;

public class EvadingGenomenPlayer extends AIController {
    // The number of frames between every update of the ai player
    public static final int UPDATE_FREQUENCY = 30;

    // The maximum length of each ray coming from the player
    public static final int MAX_RAY_LENGTH = 6;

    // The maximum length of each ray coming from the player
    public final int angleOffset = 180;

    // The current number of frames elapsed since the last update
    protected int frame = 0;

    private boolean seen = false;
    private double angle;
    private boolean left;

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
                seen = false;
            } else {
                xAxis = Math.cos(Math.toRadians(angle + angleOffset));
                yAxis = Math.sin(Math.toRadians(angle + angleOffset));
                frame = UPDATE_FREQUENCY * 2;
                seen = true;
                this.angle = angle;
                Random r = new Random();
                this.left = r.nextBoolean();
            }
            this.setAxis(xAxis, yAxis);
        }

        if (this.seen) {
            Random r = new Random();
            boolean evade = r.nextDouble() < 0.005;
            double a = r.nextDouble() * 2;
            if (evade) {
                a = 45;
            }
            if (this.left) {
                angle += a;
            } else {
                angle -= a;
            }
            double xAxis = Math.cos(Math.toRadians(angle + angleOffset));
            double yAxis = Math.sin(Math.toRadians(angle + angleOffset));
            xAxis /= Math.abs(xAxis);
            yAxis /= Math.abs(yAxis);
            this.setAxis(xAxis, yAxis);
        }

        // Next, update the AI controller
        super.update(dt);

        // Decrease the frame counter
        frame--;
    }
}
