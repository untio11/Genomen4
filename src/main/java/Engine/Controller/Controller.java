package Engine.Controller;

import GameState.Entities.Actor;

import java.util.Set;

public abstract class Controller {

    protected Actor player;

    public void setPlayer(Actor player) {
        this.player = player;
    }

    public Actor getPlayer() {
        return this.player;
    }

    public void passInput(Set<Integer> pressedKeys) {}

    public abstract void update(double dt);

}
