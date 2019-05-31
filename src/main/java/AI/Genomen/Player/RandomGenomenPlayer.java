package AI.Genomen.Player;

import Engine.Controller.AIController;

import java.util.Random;

public class RandomGenomenPlayer extends AIController {
    // The number of frames between every update of the ai player
    public static final int UPDATE_FREQUENCY = 30;

    // The current number of frames elapsed since the last update
    protected int frame = 0;

    @Override
    public void update(double dt) {

        if (frame > UPDATE_FREQUENCY) {
            Random r = new Random();
            double xAxis = r.nextDouble() * 2 - 1;
            double yAxis = r.nextDouble() * 2 - 1;
            this.setAxis(xAxis, yAxis);

            frame = 0;
        }

        // Next, update the AI controller
        super.update(dt);

        // Increase the frame counter
        frame++;
    }
}
