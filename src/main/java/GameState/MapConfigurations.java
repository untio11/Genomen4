package GameState;

import java.util.HashMap;
import java.util.Map;

public class MapConfigurations {

    public static MapConfiguration getEmptyMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.70, 0.30, 0.0, 0.0});
        chanceMap.put(1, new double[] {0.45, 0.55, 0.0, 0.0});
        chanceMap.put(10, new double[] {0.60, 0.40, 0.0, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.4, 0.60, 0.0, 0.0});
        chanceMap.put(101, new double[] {0.50, 0.50, 0.0, 0.0});
        chanceMap.put(110, new double[] {0.1, 0.9, 0.0, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.1, 0.0, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.9, 0.1, 0.0, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.6, 0.4, 0.0, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.9, 0.1, 0.0, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});

        double minPathLength = 0.1;
        double accessTerrain = 0.74;
        int mapSize = 20;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize);
    }

    public static MapConfiguration getBigEmptyMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.70, 0.30, 0.0, 0.0});
        chanceMap.put(1, new double[] {0.45, 0.55, 0.0, 0.0});
        chanceMap.put(10, new double[] {0.60, 0.40, 0.0, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.4, 0.60, 0.0, 0.0});
        chanceMap.put(101, new double[] {0.50, 0.50, 0.0, 0.0});
        chanceMap.put(110, new double[] {0.1, 0.9, 0.0, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.1, 0.0, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.9, 0.1, 0.0, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.6, 0.4, 0.0, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.9, 0.1, 0.0, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});

        double minPathLength = 0.1;
        double accessTerrain = 0.74;
        int mapSize = 50;
        int startRadius = 20;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize, startRadius);
    }

    public static MapConfiguration getStarterMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.65, 0.30, 0.05, 0.0});
        chanceMap.put(1, new double[] {0.0, 0.55, 0.0, 0.45});
        chanceMap.put(10, new double[] {0.55, 0.40, 0.05, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.35, 0.55, 0.05, 0.05});
        chanceMap.put(101, new double[] {0.0, 0.50, 0.0, 0.50});
        chanceMap.put(110, new double[] {0.05, 0.85, 0.05, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.05, 0.05, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.85, 0.1, 0.05, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.48, 0.47, 0.05, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.6, 0.1, 0.3, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});

        double minPathLength = 0.5;
        double accessTerrain = 0.74;
        int mapSize = 25;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize);
    }

    public static MapConfiguration getBigStarterMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.65, 0.30, 0.05, 0.0});
        chanceMap.put(1, new double[] {0.0, 0.1, 0.0, 0.9});
        chanceMap.put(10, new double[] {0.55, 0.40, 0.05, 0.0});
        chanceMap.put(11, new double[] {0.0, 0.7, 0.0, 0.3});
        chanceMap.put(100, new double[] {0.3, 0.6, 0.0, 0.1});
        chanceMap.put(101, new double[] {0.0, 0.50, 0.0, 0.50});
        chanceMap.put(110, new double[] {0.05, 0.85, 0.05, 0.0});
        chanceMap.put(111, new double[] {0.0, 0.3, 0.0, 0.7});
        chanceMap.put(1000, new double[] {0.90, 0.05, 0.05, 0.0});
        chanceMap.put(1001, new double[] {0.0, 0.8, 0.0, 0.2});
        chanceMap.put(1010, new double[] {0.85, 0.1, 0.05, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.5, 0.5, 0.0, 0.0});
        chanceMap.put(1101, new double[] {0.0, 0.7, 0.0, 0.3});
        chanceMap.put(1110, new double[] {0.6, 0.2, 0.2, 0.0});
        chanceMap.put(1111, new double[] {0.0, 0.9, 0.0, 0.1});

        double minPathLength = 0.1;
        double accessTerrain = 0.74;
        int mapSize = 50;
        int startRadius = 20;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize, startRadius);
    }

    public static MapConfiguration getVerySimpleMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.65, 0.30, 0.05, 0.0});
        chanceMap.put(1, new double[] {0.0, 0.55, 0.0, 0.45});
        chanceMap.put(10, new double[] {0.55, 0.35, 0.10, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.35, 0.40, 0.20, 0.05});
        chanceMap.put(101, new double[] {0.0, 0.50, 0.0, 0.50});
        chanceMap.put(110, new double[] {0.05, 0.85, 0.05, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.05, 0.05, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.75, 0.05, 0.2, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.48, 0.47, 0.05, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.5, 0.30, 0.2, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});

        double minPathLength = 1.0;
        double accessTerrain = 0.74;
        int mapSize = 30;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize);
    }

    public static MapConfiguration getSimpleMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.39, 0.30, 0.30, 0.01});
        chanceMap.put(1, new double[] {0.0, 0.55, 0.0, 0.45});
        chanceMap.put(10, new double[] {0.35, 0.35, 0.30, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.35, 0.40, 0.20, 0.05});
        chanceMap.put(101, new double[] {0.0, 0.50, 0.0, 0.50});
        chanceMap.put(110, new double[] {0.05, 0.85, 0.05, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.05, 0.05, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.48, 0.05, 0.47, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.48, 0.47, 0.05, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.34, 0.33, 0.33, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});

        double minPathLength = 1.0;
        double accessTerrain = 0.74;
        int mapSize = 30;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize);
    }

    public static MapConfiguration getNormalMap() {
        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.39, 0.30, 0.30, 0.01});
        chanceMap.put(1, new double[] {0.0, 0.15, 0.0, 0.85});
        chanceMap.put(10, new double[] {0.05, 0.05, 0.90, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.35, 0.30, 0.20, 0.15});
        chanceMap.put(101, new double[] {0.0, 0.50, 0.0, 0.50});
        chanceMap.put(110, new double[] {0.05, 0.85, 0.05, 0.0});
        chanceMap.put(111, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1000, new double[] {0.90, 0.05, 0.05, 0.0});
        chanceMap.put(1001, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1010, new double[] {0.48, 0.05, 0.47, 0.0});
        chanceMap.put(1011, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1100, new double[] {0.48, 0.47, 0.05, 0.0});
        chanceMap.put(1101, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(1110, new double[] {0.34, 0.33, 0.33, 0.0});
        chanceMap.put(1111, new double[] {0.0, 1, 0.0, 0.0});


        double minPathLength = 1.1;
        double accessTerrain = 0.74;
        int mapSize = 60;

        return new MapConfiguration(chanceMap, minPathLength, accessTerrain, mapSize);
    }
}
