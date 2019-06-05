package util;

public interface Observable {
    /**
     * Add an observer to the list of observers. These will be notified with the update() call.
     * @param obs Object that implements the observer interface.
     */
    void add(Observer obs);

    /**
     * Remove the observer from the list
     * @param obs Object that implements the observer interface
     */
    void remove(Observer obs);

    /**
     * Notify all observers of a change in the observable.
     */
    void broadcast();
}
