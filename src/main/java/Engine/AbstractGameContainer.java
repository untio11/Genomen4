package Engine;

import Engine.Controller.Controller;

public interface AbstractGameContainer {

    void setKidnapperPlayer();

    void setFatherPlayer();

    void setFatherAI(Controller c);

    void setKidnapperAI(Controller c);

    void start();

    double getRemainingTime();

    boolean isFatherWin();
}
