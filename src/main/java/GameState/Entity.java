package GameState;

public class Entity {
    protected Position<Float> position;

    public Position<Integer> getDiscretePosition() {
        return new Position<>(position.getX().intValue(), position.getY().intValue());
    }

    public Position<Float> getPosition() {
        return position.copy();
    }

    public void setPosition(Position new_pos) {
        this.position.setPosition(new_pos.getX().floatValue(), new_pos.getY().floatValue());
    }
}
