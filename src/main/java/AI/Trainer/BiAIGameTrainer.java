package AI.Trainer;

import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BiAIGameTrainer<A extends TrainerAIPlayer, B extends TrainerAIPlayer, G> extends BaseAIGameTrainer<G> {

    private List<A> players1;
    private List<B> players2;

    private List<Pair<A, B>> competition;

    private Map<A, Integer> playerScores1;
    private Map<B, Integer> playerScores2;

    private GeneticAlgorithm<A> geneticAlgorithm1;
    private GeneticAlgorithm<B> geneticAlgorithm2;

    public BiAIGameTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
        players1 = new ArrayList<>();
        players2 = new ArrayList<>();
        playerScores1 = new HashMap<>();
        playerScores2 = new HashMap<>();

        final BiAIGameTrainer<A, B, G> trainer = this;

        geneticAlgorithm1 = new GeneticAlgorithm<>(new AIPlayerBuilder<A>() {

            @Override
            public A createPlayer(Map<String, INDArray> paramTable) {
                return trainer.createPlayer1(paramTable);
            }
        });

        geneticAlgorithm2 = new GeneticAlgorithm<>(new AIPlayerBuilder<B>() {

            @Override
            public B createPlayer(Map<String, INDArray> paramTable) {
                return trainer.createPlayer2(paramTable);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        this.createPlayers1(players1);
        this.createPlayers2(players2);
    }

    @Override
    protected void setupCompetition() {
        competition = this.createCompetition(players1, players2);
    }

    @Override
    protected void playCompetition() {

    }

    @Override
    protected void evaluateCompetition() {

    }

    @Override
    protected void performGeneticEvolution() {

    }

    @Override
    protected void resetGeneticAlgorithm() {

    }


    protected void createPlayers1(List<A> players1) {
        for (int i = 0; i < this.nPlayers; i++) {
            A player = this.createPlayer1();
            players1.add(player);
            playerScores1.put(player, 0);
        }
    }

    protected void createPlayers2(List<B> players2) {
        for (int i = 0; i < this.nPlayers; i++) {
            B player = this.createPlayer2();
            players2.add(player);
            playerScores2.put(player, 0);
        }
    }

    protected abstract A createPlayer1();

    protected abstract A createPlayer1(Map<String, INDArray> paramTable);

    protected abstract B createPlayer2();

    protected abstract B createPlayer2(Map<String, INDArray> paramTable);

    protected abstract List<Pair<A, B>> createCompetition(List<A> players1, List<B> players2);

    protected abstract G createGame(Pair<A, B> players);
}
