package GameState;

public class Tile {

    private Position position;
    private TileType type;
    private int heuristic;
    private int gCost;
    private int fScore;
    private Tile parent;

    /**
     * Constructor for a tile required a type and a height.
     */
    public Tile(TileType t, int[] coordinates, int heuristic) {
        this.type = t;
        this.position = new Position(coordinates[1], coordinates[0]); // row -> y, column -> x
        this.heuristic = heuristic;
    }

    public boolean isAccessible() {
        return type.isAccessible(type);
    }

    public TileType getType() {
        return type;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public int getRow() {
        return position.y;
    }

    public int getColumn() {
        return position.x;
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
        return parent;
    }

    public void setParent(Tile t) {
        this.parent = t;
    }

    public Position getPosition() {
        return position;
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
