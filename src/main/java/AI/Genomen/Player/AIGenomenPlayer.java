package AI.Genomen.Player;

import AI.Trainer.TrainerAIPlayer;
import GameState.Entities.Actor;
import Graphics.Models.TexturedModel;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;

public class AIGenomenPlayer extends Actor implements TrainerAIPlayer {
    /**
     * Initialize a player with the appropriate properties
     *
     * @param model    The model that the player should have: We probably want to change this to some loose reference
     * @param position The position of the player
     * @param rotX     The rotation around the x-axis
     * @param rotY     The rotation around the y-axis
     * @param rotZ     The rotation around the z-axis
     * @param scale    The size of the model (I think)
     */
    public AIGenomenPlayer(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    @Override
    public MultiLayerNetwork getNetwork() {
        return null;
    }

    @Override
    public void saveNetwork(File f) throws IOException {

    }

    @Override
    public MultiLayerNetwork loadNetwork(File f) throws IOException {
        return null;
    }
}
