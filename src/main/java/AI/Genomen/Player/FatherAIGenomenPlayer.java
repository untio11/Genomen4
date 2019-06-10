package AI.Genomen.Player;

import java.io.File;
import java.io.IOException;

public class FatherAIGenomenPlayer extends AIGenomenPlayer {


    public FatherAIGenomenPlayer(File f) {
        try {
            this.loadNetwork(f);

        } catch (IOException e) {
            System.err.println("Could not load file " + f.getName());
            this.init();
        }
    }

    @Override
    public int getUpdateFrequency() {
        return 30;
    }

    @Override
    public int getInputCount() {
        return 8;
    }

    @Override
    public int getRememberCount() {
        return 3;
    }

    @Override
    public int getMaxRayLength() {
        return 6;
    }
}
