package GameState;

/**
 * Represents a location in the world.
 * @param <T> The type of position
 */
public class Position<T extends Number> {
    public T x;
    protected T y;

    public void setPosition(T x, T y) {
        this.x = x;
        this.y = y;
    }

    public void setX(T x) {
        this.x = x;
    }

    public void setY(T y) {
        this.x = y;
    }



    public T getX() {
        return x;
    }

    public T getY() {
        return y;
    }

    public Position(T x, T y) {
        setPosition(x, y);
    }

    Position<T> copy() {
        return new Position<>(this.x, this.y);
    }
}
