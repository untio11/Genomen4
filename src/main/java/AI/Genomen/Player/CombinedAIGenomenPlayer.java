package AI.Genomen.Player;

import Engine.Controller.AIController;
import GameState.Entities.Actor;

import java.io.File;

public class CombinedAIGenomenPlayer extends AIController {

    // The number of frames between every update of the ai player
    protected final int updateFrequency = 30;

    // The maximum length of each ray coming from the player
    protected final int maxRayLength = 6;

    // The current number of frames elapsed since the last update
    protected int frame = 0;

    private AIGenomenPlayer searchingPlayer;

    private AIGenomenPlayer catchingPlayer;

    public CombinedAIGenomenPlayer() {
        super();
        File searchingFile = new File("res/network/father/1560138928134-single-genomen-1-8986.net");
        GenomenAISettings searchingSettings = new GenomenAISettings();
        searchingSettings.setInputCount(8).setRememberCount(3).setUpdateFrequency(10);
        searchingPlayer = new LoadAIGenomenPlayer(searchingFile, searchingSettings);

        File catchingFile = new File("res/network/father/01-single-genomen-1-4092.net");
        GenomenAISettings catchingSettings = new GenomenAISettings();
        catchingSettings.setInputCount(0).setRememberCount(2).setUpdateFrequency(10);
        catchingPlayer = new LoadAIGenomenPlayer(catchingFile, catchingSettings);
    }

    @Override
    public void setPlayer(Actor player) {
        super.setPlayer(player);
        searchingPlayer.setPlayer(player);
        catchingPlayer.setPlayer(player);
    }

    @Override
    public void update(double dt) {
        // First check if the AI should update the movement axes
        if (frame > getUpdateFrequency()) {
            // If so, update these
            this.movePlayer();
            frame = 0;
        }

        // Next, update the AI controller
        super.update(dt);

        // Increase the frame counter
        frame++;
    }

    protected void movePlayer() {
        double[][] input = this.getInput(0, getMaxRayLength());
        boolean enemyVisible = input[0][0] == 2;
        AIGenomenPlayer controller;
        System.out.println(enemyVisible);
        if (enemyVisible) {
            controller = catchingPlayer;
        } else {
            controller = searchingPlayer;
        }

        controller.movePlayer();

        double xAxis = controller.getXAxis();
        double yAxis = controller.getYAxis();
        boolean boost = controller.isBoost();

        this.setAxis(xAxis, yAxis);
        this.setBoost(boost);
    }

    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public int getMaxRayLength() {
        return maxRayLength;
    }
}
