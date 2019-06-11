package AI.Genomen.Player;

public class GenomenAISettings {

    int updateFrequency = 30;
    int inputCount = 8;
    int rememberCount = 3;
    int maxRayLength = 6;

    public int getUpdateFrequency() {
        return updateFrequency;
    }

    public int getInputCount() {
        return inputCount;
    }

    public int getRememberCount() {
        return rememberCount;
    }

    public int getMaxRayLength() {
        return maxRayLength;
    }

    public GenomenAISettings setUpdateFrequency(int updateFrequency) {
        this.updateFrequency = updateFrequency;
        return this;
    }

    public GenomenAISettings setInputCount(int inputCount) {
        this.inputCount = inputCount;
        return this;
    }

    public GenomenAISettings setRememberCount(int rememberCount) {
        this.rememberCount = rememberCount;
        return this;
    }

    public GenomenAISettings setMaxRayLength(int maxRayLength) {
        this.maxRayLength = maxRayLength;
        return this;
    }
}