package GameState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MapGenerator {

    private int worldHeight;
    private int worldWidth;
    private Tile map[][];

    public Tile[][] generate(int width, int height) {

        worldWidth = width;
        worldHeight = height;

        map = new Tile[width][height];

        for (int r = 0; r < width; ++r) {
            for (int c = 0; c < height; ++c) {
                if (isWorldEdge(r, c)) {
                    map[r][c] = getBayesianTile(r, c);
                }
            }
        }

        //First fill a skeleton of the map to increase randomness
        for (int r = 0; r < width; r = r + 3) {
            for (int c = 0; c < height; c = c + 3) {
                map[r][c] = getBayesianTile(r, c);
            }
        }

        //First fill middle block to prevent influence from water at the sides
        for (int r = 4; r < width - 4; ++r) {
            for (int c = 4; c < height - 4; ++c) {
                if (map[r][c] == null) {
                    map[r][c] = getBayesianTile(r, c);
                }
            }
        }

        //Then fill in all of the holes
        for (int r = 0; r < width; ++r) {
            for (int c = 0; c < height; ++c) {
                if (map[r][c] == null) {
                    map[r][c] = getBayesianTile(r, c);
                }
            }
        }



        return map;
    }

    private Tile getBayesianTile(int r, int c) {
        if (isWorldEdge(r, c)) {
            return new Tile(TileType.WATER, 0);
        }

        double chance[] = getChanceDistribution2(r, c);
        double random = Math.random();
        if (random <= chance[0]) {
            return  new Tile(TileType.GRASS, 0);
        } else if (random <= chance[0] + chance[1]) {
            return new Tile(TileType.SAND, 0);
        } else if (random <= chance[0] + chance[1] + chance[2] ) {
            return new Tile(TileType.TREE, 0);
        } else {
            return new Tile(TileType.WATER, 0);
        }

    }

    private Boolean isWorldEdge(int r, int c) {
        return (r == 0 || c == 0 || r == worldHeight - 1 || c == worldWidth - 1);
    }

    /**
     * An array containing all TileTypes a tile is connected to
     * @param r row of the tile
     * @param c column of the tile
     * @return TileType[] containing all TileTypes this tile is connected to.
     * @throws IllegalArgumentException if tile if at world edge. These should always be water anyway.
     */
    private TileType[] connectedTo(int r, int c){
        //Create HashSet to store types and immediately remove duplicates
        HashSet<TileType> neighbourTypes= new HashSet<>();
        if (isWorldEdge(r, c)) {
            throw new IllegalArgumentException("Tile is at world edge");
        }

        for (int[] neighbour : neighbours(r, c)) {
            Tile neighbourTile = map[neighbour[0]][neighbour[1]];
            if (neighbourTile != null) {
                neighbourTypes.add(neighbourTile.getType());
            }
        }

        return neighbourTypes.toArray(new TileType[0]);
    }

    /**
     * A double array containing the neighbours of a tile, in the order North, East, South, West.
     * @param r row of the tile
     * @param c column of the tile
     * @return TileType[] containing all TileTypes this tile is connected to.
     * @throws IllegalArgumentException if tile if at world edge. These tiles are not interesting anyway, always water.
     */
    private int[][] neighbours(int r, int c) {
        if (isWorldEdge(r, c)) {
            throw new IllegalArgumentException("Tile is at world edge");
        }
        int [][] neighbours = new int[4][2]; //four arrays of neighbour coordinates.
        int[] northNeighbour = {r - 1, c};
        int[] eastNeighbour = {r, c + 1};
        int[] southNeighbour = {r + 1, c};
        int[] westNeighbour = {r, c - 1};

        neighbours[0] = northNeighbour;
        neighbours[1] = eastNeighbour;
        neighbours[2] = southNeighbour;
        neighbours[3] = westNeighbour;

        return neighbours;
    }


    public String toString() {
        return Arrays.deepToString(map).replaceAll("], ", "]\n").substring(1).replaceAll(", ", ",\t");
    }

    public Tile[][] getMap() {
        return map;
    }

    public int[] getWorldDimensions() {
        return new int[] {worldWidth, worldHeight};
    }


    /**
     * Old method for generating a chance distribution
     * @param r row of the tile
     * @param c column of the tile
     * @return a Double array containing chances for grass, sand, tree, water.
     */
    private Double[] getChanceDistribution(int r, int c) {
        double toDistribute = 0.6;
        TileType[] connectedTypes = connectedTo(r, c);
        int numConnectedTypes = connectedTypes.length;
        Map<TileType, Double> chanceMap = new HashMap<>();
        chanceMap.put(TileType.GRASS, 0.1);
        chanceMap.put(TileType.SAND, 0.1);
        chanceMap.put(TileType.TREE, 0.1);
        chanceMap.put(TileType.WATER, 0.1);

        for (TileType t : connectedTypes) {
            chanceMap.put(t, chanceMap.get(t) + toDistribute / numConnectedTypes);
        }

        //Then still all are 0.1, since the for loop did not do anything, we give all equal chance.
        if (numConnectedTypes == 0) {
            for (TileType t : chanceMap.keySet()) {
                chanceMap.put(t, chanceMap.get(t) + toDistribute / 4);
            }
        }

        return chanceMap.values().toArray(new Double[0]);
    }


    /**
     * Method for generating a chance distribution
     * @param r row of the tile
     * @param c column of the tile
     * @return a double array containing chances for grass, sand, tree, water.
     */
    private double[] getChanceDistribution2(int r, int c) {
        TileType[] connectedTypes = connectedTo(r, c);

        //create the bayesian network
        Map<Integer, double[]> chanceMap = new HashMap<>();
        chanceMap.put(0, new double[] {0.30, 0.30, 0.30, 0.10});
        chanceMap.put(1, new double[] {0.0, 0.15, 0.0, 0.85});
        chanceMap.put(10, new double[] {0.05, 0.05, 0.90, 0.0});
        chanceMap.put(11, new double[] {0.0, 1, 0.0, 0.0});
        chanceMap.put(100, new double[] {0.05, 0.45, 0.05, 0.45});
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

        int keyToLookFor = (Arrays.asList(connectedTypes).contains(TileType.GRASS) ? 1000 : 0) +
                (Arrays.asList(connectedTypes).contains(TileType.SAND) ? 100 : 0) +
                (Arrays.asList(connectedTypes).contains(TileType.TREE) ? 10 : 0) +
                (Arrays.asList(connectedTypes).contains(TileType.WATER) ? 1 : 0);

        return chanceMap.get(keyToLookFor);
    }

}
