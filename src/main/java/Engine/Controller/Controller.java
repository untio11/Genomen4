package Engine.Controller;

import GameState.Entities.Actor;

public abstract class Controller {

    protected Actor player;

    public void setPlayer(Actor player) {
        this.player = player;
    }

    public abstract void update(double dt);

}