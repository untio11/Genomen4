package GameState;

import java.util.Map;

public class MapConfiguration {

    private Map<Integer, double[]> chanceMap;
    private double minPathLength;
    private double accessTerain;
    private int mapSize;

    public MapConfiguration(Map<Integer, double[]> chanceMap, double minPathLength, double accessTerrain, int mapSize) {
        this.chanceMap = chanceMap;
        this.minPathLength = minPathLength;
        this.accessTerain = accessTerrain;
        this.mapSize = mapSize;
    }

    public Map<Integer, double[]> getChanceMap() {
        return chanceMap;
    }

    public double getMinPathLength() {
        return minPathLength;
    }

    public double getAccessTerain() {
        return accessTerain;
    }

    public int getMapSize() {
        return mapSize;
    }
}
