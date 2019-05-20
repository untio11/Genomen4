package AI.Trainer;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;

import java.io.File;
import java.io.IOException;

public interface TrainerAIPlayer {

    MultiLayerNetwork getNetwork();
    void saveNetwork(File f) throws IOException;
    MultiLayerNetwork loadNetwork(File f) throws IOException;
}
