package GameState;

public class Tile {

    private TileType type;
    private float height;
    private int[] coordinates;
    private int heuristic;
    private int gCost;
    private int fScore;
    private Tile parent;

    public boolean isAccessible() {
        return type.isAccessible(type);
    }

    public float getHeight() {
        return height;
    }

    public TileType getType() {
        return type;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public int getRow() {
        return coordinates[0];
    }

    public int getColumn() {
        return coordinates[1];
    }

    public int getgCost() {
        return gCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public int getfScore() {
        return fScore;
    }

    public void setfScore(int fScore) {
        this.fScore = fScore;
    }

    public Tile getParent() {
        return  parent;
    }

    public void setParent(Tile t){
        this.parent = t;
    }

    /**
     * Constructor for a tile required a type and a height.
     */
    Tile(TileType t, int height, int[] coordinates, int heuristic) {
        this.type = t;
        this.height = height;
        this.coordinates = coordinates;
        this.heuristic = heuristic;
    }

    @Override
    public String toString() {
        switch (type) {
            case GRASS:
                return "Grass";
            case SAND:
                return "Sand";
            case WATER:
                return "Water";
            case TREE:
                return "Tree";
            default:
                return this.getType().toString();
        }
    }
}
