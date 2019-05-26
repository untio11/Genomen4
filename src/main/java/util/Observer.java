package util;

public interface Observer<T extends Observable> {
    /**
     * Process the update in the thing that is being observed.
     * @param observable The object that is being observed.
     */
    void update(T observable);
}
