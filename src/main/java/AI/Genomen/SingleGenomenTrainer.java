package AI.Genomen;

import AI.Genomen.Player.AIGenomenPlayer;
import AI.Genomen.Player.RandomGenomenPlayer;
import AI.Genomen.Player.SimpleGenomenPlayer;
import AI.Trainer.SingleBiAIGameTrainer;
import Engine.Controller.Controller;
import Engine.GameContainer;
import GameState.MapConfiguration;
import GameState.MapConfigurations;
import GameState.World;
import org.joml.Vector3f;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import javax.naming.ldap.Control;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SingleGenomenTrainer extends SingleBiAIGameTrainer<AIGenomenPlayer, Controller, GameContainer> {

    // Boolean for choosing between the father and kidnapper
    private boolean fatherAI = true;

    private static final int GAMES = 4;

    private static final int WINNING_FACTOR = 4;

    private static final int TIME_FACTOR = 2;

    private static MapConfiguration mapConfig = MapConfigurations.getBigEmptyMap();

    public SingleGenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return "single-genomen";
    }

    public static void main(String[] args) {
        int players = 100;
        SingleGenomenTrainer trainer = new SingleGenomenTrainer(players, 10);

        long startTime = System.nanoTime();

        trainer.init();
        trainer.runGeneticAlgorithm();

        long endTime = System.nanoTime();
        long durationNano = endTime - startTime;
        long durationSecs = TimeUnit.NANOSECONDS.toSeconds(durationNano);

        System.out.println("Genetic algorithm duration: " +  durationSecs + " seconds");

        LinkedHashMap<AIGenomenPlayer, Integer> sortedPlayers = trainer.getScoredPlayers1();

        // Play against the best father player
        World.initWorld(mapConfig);
        final GameContainer game = new GameContainer(World.getInstance(), true);
        Controller fatherAI = sortedPlayers.entrySet().iterator().next().getKey();
        fatherAI.setPlayer(World.getInstance().getFather());
        game.setFatherAI(fatherAI);
        game.setKidnapperPlayer();
        game.start();
    }

    @Override
    protected AIGenomenPlayer createPlayer1() {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        return player;
    }

    @Override
    protected AIGenomenPlayer createPlayer1(Map<String, INDArray> paramTable) {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected void createPlayers2(List<Controller> players) {
        players.add(new SimpleGenomenPlayer());
    }

    @Override
    protected List<Pair<AIGenomenPlayer, Controller>> createCompetition(List<AIGenomenPlayer> players1, List<Controller> players2) {
        List<Pair<AIGenomenPlayer, Controller>> competition = new ArrayList<>();

        // Create a bipartite graph as the competition
        for (AIGenomenPlayer player1 : players1) {
            for (Controller player2 : players2) {
                for (int i = 0; i < GAMES; i++) {
                    competition.add(new Pair<>(player1, player2));
                }
            }
        }

        return competition;
    }

    @Override
    protected GameContainer createGame(Pair<AIGenomenPlayer, Controller> players) {
        World.initWorld(mapConfig);
        GameContainer gc = new GameContainer(World.getInstance(), false);
        players.getFirst().setPlayer(World.getInstance().getFather());
        gc.setFatherAI(players.getFirst());
        players.getSecond().setPlayer(World.getInstance().getKidnapper());
        gc.setKidnapperAI(players.getSecond());
        return gc;
    }

    @Override
    protected void playGame(GameContainer game) {
        game.start();

        boolean fatherWins = game.isFatherWin();
        int roundTime = (int) game.getRoundTime();
        int remainingTime = Math.min(roundTime, Math.max(0, (int) game.getRemainingTime()));
        int fatherScore = 0;
        int kidnapperScore = 0;

        if (fatherWins) {
            fatherScore += roundTime * WINNING_FACTOR;
        } else {
            kidnapperScore += roundTime * WINNING_FACTOR;
        }
        fatherScore += remainingTime * TIME_FACTOR;
        kidnapperScore += (roundTime - remainingTime) * TIME_FACTOR;

        Controller fatherPlayer = game.getFatherController();
        Vector3f fatherPos = fatherPlayer.getPlayer().getPosition();
        Controller kidnapperPlayer = game.getKidnapperController();
        Vector3f kidnapperPos = kidnapperPlayer.getPlayer().getPosition();

        int distance = (int) fatherPos.distance(kidnapperPos);

        kidnapperScore += distance;
        fatherScore += game.getMaxDistance() - distance;

        if (fatherAI) {
            this.setResults1((AIGenomenPlayer) fatherPlayer, fatherScore);
        } else {
            this.setResults1((AIGenomenPlayer) kidnapperPlayer, kidnapperScore);
        }
    }
}
