package GameState;

public class Entity {
    private Position<Float> position;
    private Position<Integer> tilePosition;
    private Position<Float> offset;
    private int size;

    public Position<Integer> getDiscretePosition() {
        return new Position<>(Math.round(position.getX()), Math.round(position.getY()));
    }

    public Position<Float> getPosition() {
        return position.copy();
    }

    public Position<Integer> getTilePosition() {
        return tilePosition.copy();
    }

    public Position<Float> getOffset() {
        return offset.copy();
    }

    public void setPosition(float x, float y) {
        position.setPosition(x,y);
    }

    public void setTilePosition(int x, int y) {
        tilePosition.setPosition(x,y);
    }

    public void setOffset(float x, float y) {
        offset.setPosition(x,y);
    }

    public void setSize(int size) {
        this.size = size;
    }
}
