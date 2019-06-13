package AI.Genomen.Player;

import java.io.File;
import java.io.IOException;

public class LoadAIGenomenPlayer extends AIGenomenPlayer {

    private GenomenAISettings settings;

    public LoadAIGenomenPlayer(File f) {
        super();
        settings = new GenomenAISettings();
        this.loadNetworkOrInit(f);
    }

    public LoadAIGenomenPlayer(File f, GenomenAISettings settings) {
        super();
        this.settings = settings;
        this.loadNetworkOrInit(f);
    }

    private void loadNetworkOrInit(File f) {
        try {
            this.loadNetwork(f);
        } catch (IOException e) {
            System.err.println("Could not load file " + f.getName());
        }
        this.init();
    }

    @Override
    public int getUpdateFrequency() {
        return settings.updateFrequency;
    }

    @Override
    public int getInputCount() {
        return settings.inputCount;
    }

    @Override
    public boolean isAddBoost() {
        return settings.addBoost;
    }

    @Override
    public int getRememberCount() {
        return settings.rememberCount;
    }

    @Override
    public int getMaxRayLength() {
        return settings.maxRayLength;
    }
}