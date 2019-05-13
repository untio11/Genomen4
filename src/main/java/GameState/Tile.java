package GameState;

public class Tile extends Entity {

    private TileType type;
    private float height;
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

    public int getHeuristic() {
        return heuristic;
    }

    public int getRow() {
        return position.getY().intValue();
    }

    public int getColumn() {
        return position.getX().intValue();
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
        this.position = new Position<>((float) coordinates[1], (float) coordinates[0]); // row -> y, column -> x
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
                return "Shore";
        }
    }
}
