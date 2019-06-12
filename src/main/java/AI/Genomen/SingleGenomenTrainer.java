package AI.Genomen;

import AI.Genomen.Player.*;
import AI.Trainer.SingleBiAIGameTrainer;
import Engine.Controller.Controller;
import Engine.GameContainerSwing;
import GameState.MapConfiguration;
import GameState.MapConfigurations;
import GameState.World;
import org.joml.Vector3f;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class SingleGenomenTrainer extends SingleBiAIGameTrainer<AIGenomenPlayer, Controller, GameContainerSwing> {

    // Boolean for choosing between the father and kidnapper
    private static boolean fatherAI = true;

    private static final int GAMES = 6;
    private long[] worldSeeds = new long[GAMES];

    private static final int WINNING_FACTOR = 4;

    private static final int TIME_FACTOR = 2;

    private static MapConfiguration mapConfig = MapConfigurations.getBigStarterMap();

    public SingleGenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        if (fatherAI) {
            return "single-genomen-father";
        } else {
            return "single-genomen-kidnapper";
        }
    }

    public static void main(String[] args) {
        int players = 100;
        SingleGenomenTrainer trainer = new SingleGenomenTrainer(players, 1000);

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
        final GameContainerSwing game = new GameContainerSwing(World.getInstance(), true);
        if (fatherAI) {
            Controller fatherAI = sortedPlayers.entrySet().iterator().next().getKey();
            fatherAI.setPlayer(World.getInstance().getFather());
            game.setFatherAI(fatherAI);
            game.setKidnapperPlayer();
        } else {
            Controller kidnapperAI = sortedPlayers.entrySet().iterator().next().getKey();
            kidnapperAI.setPlayer(World.getInstance().getKidnapper());
            game.setKidnapperAI(kidnapperAI);
            game.setFatherPlayer();
        }
        game.start();
    }

    @Override
    protected AIGenomenPlayer createPlayer1() {
        AIGenomenPlayer player = new AIGenomenPlayer(true);
        player.init();
        return player;
    }

    @Override
    protected AIGenomenPlayer createPlayer1(Map<String, INDArray> paramTable) {
        AIGenomenPlayer player = new AIGenomenPlayer(true);
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected void createPlayers2(List<Controller> players) {
        players.add(new StaticGenomenPlayer());
        players.add(new RandomGenomenPlayer());
        players.add(new SimpleGenomenPlayer(false));
//        players.add(new SimpleGenomenPlayer(true, 30));
//        players.add(new LoadAIGenomenPlayer(new File("res/network/1560138909372-single-genomen-1-9006.net")));
        players.add(new EvadingGenomenPlayer());
    }

    @Override
    protected List<Pair<AIGenomenPlayer, Controller>> createCompetition(List<AIGenomenPlayer> players1, List<Controller> players2) {
        List<Pair<AIGenomenPlayer, Controller>> competition = new ArrayList<>();

        Random r = new Random();

        for (int i = 0; i < GAMES; i++) {
            worldSeeds[i] = r.nextLong();
        }

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
    protected GameContainerSwing createGame(Pair<AIGenomenPlayer, Controller> players, int gameId) {
        long seed = worldSeeds[gameId % GAMES];
        World.initWorld(mapConfig, seed);
        GameContainerSwing gc = new GameContainerSwing(World.getInstance(), false);
        if (fatherAI) {
            players.getFirst().setPlayer(World.getInstance().getFather());
            gc.setFatherAI(players.getFirst());
            players.getSecond().setPlayer(World.getInstance().getKidnapper());
            gc.setKidnapperAI(players.getSecond());
        } else {
            players.getFirst().setPlayer(World.getInstance().getKidnapper());
            gc.setKidnapperAI(players.getFirst());
            players.getSecond().setPlayer(World.getInstance().getFather());
            gc.setFatherAI(players.getSecond());
        }
        return gc;
    }

    @Override
    protected void playGame(GameContainerSwing game) {
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
