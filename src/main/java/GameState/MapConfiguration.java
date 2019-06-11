package GameState;

import java.util.Map;

public class MapConfiguration {

    private Map<Integer, double[]> chanceMap;
    private double minPathLength;
    private double accessTerain;
    private int mapSize;
    private int startRadius;

    public MapConfiguration(Map<Integer, double[]> chanceMap, double minPathLength, double accessTerrain, int mapSize) {
        this.chanceMap = chanceMap;
        this.minPathLength = minPathLength;
        this.accessTerain = accessTerrain;
        this.mapSize = mapSize;
        this.startRadius = 2;
    }

    public MapConfiguration(Map<Integer, double[]> chanceMap, double minPathLength, double accessTerrain, int mapSize, int startRadius) {
        this.chanceMap = chanceMap;
        this.minPathLength = minPathLength;
        this.accessTerain = accessTerrain;
        this.mapSize = mapSize;
        this.startRadius = startRadius;
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

    public int getStartRadius() {
        return startRadius;
    }
}
