package GameState;

public class Entity {
    protected Position<Float> position;

    public Position<Integer> getDiscretePosition() {
        return new Position<>(Math.round(position.getX()), Math.round(position.getY()));
    }

    /*public Position<Float> getPosition() {
        return position.copy();
    }*/
}
