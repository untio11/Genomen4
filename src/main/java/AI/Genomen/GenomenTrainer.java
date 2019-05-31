package AI.Genomen;

import AI.Genomen.Player.AIGenomenPlayer;
import AI.Trainer.BiAIGameTrainer;
import Engine.Controller.Controller;
import Engine.GameContainer;
import GameState.MapConfiguration;
import GameState.MapConfigurations;
import GameState.World;
import org.joml.Vector3f;
import org.nd4j.linalg.api.ndarray.INDArray;
import util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GenomenTrainer extends BiAIGameTrainer<AIGenomenPlayer, AIGenomenPlayer, GameContainer> {

    private static MapConfiguration mapConfig = MapConfigurations.getEmptyMap();

    public GenomenTrainer(int nPlayers, int iterations) {
        super(nPlayers, iterations);
    }

    @Override
    protected String getName() {
        return "genomen";
    }

    public static void main(String[] args) {
        int players = 50;
        GenomenTrainer trainer = new GenomenTrainer(players, 30);

        long startTime = System.nanoTime();

        trainer.init();
        trainer.runGeneticAlgorithm();

        long endTime = System.nanoTime();
        long durationNano = endTime - startTime;
        long durationSecs = TimeUnit.NANOSECONDS.toSeconds(durationNano);

        System.out.println("Genetic algorithm duration: " +  durationSecs + " seconds");

        LinkedHashMap<AIGenomenPlayer, Integer> sortedPlayers = trainer.getScoredPlayers2();

        // Play against the best father player
        World.initWorld(mapConfig);
        final GameContainer game = new GameContainer(World.getInstance(), true);
        Controller kidnapperAI = sortedPlayers.entrySet().iterator().next().getKey();
        kidnapperAI.setPlayer(World.getInstance().getKidnapper());
        game.setKidnapperAI(kidnapperAI);
        game.setFatherPlayer();
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
    protected AIGenomenPlayer createPlayer2() {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        return player;
    }

    @Override
    protected AIGenomenPlayer createPlayer2(Map<String, INDArray> paramTable) {
        AIGenomenPlayer player = new AIGenomenPlayer();
        player.init();
        player.getNetwork().setParamTable(paramTable);
        return player;
    }

    @Override
    protected List<Pair<AIGenomenPlayer, AIGenomenPlayer>> createCompetition(List<AIGenomenPlayer> players1, List<AIGenomenPlayer> players2) {
        List<Pair<AIGenomenPlayer, AIGenomenPlayer>> competition = new ArrayList<>();

        // Create a bipartite graph as the competition
        for (AIGenomenPlayer player1 : players1) {
            for (AIGenomenPlayer player2 : players2) {
                competition.add(new Pair<>(player1, player2));
            }
        }

        return competition;
    }

    @Override
    protected GameContainer createGame(Pair<AIGenomenPlayer, AIGenomenPlayer> players) {
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
            fatherScore += roundTime;
        } else {
            kidnapperScore += roundTime;
        }
        fatherScore += remainingTime;
        kidnapperScore += roundTime - remainingTime;

        AIGenomenPlayer fatherPlayer = (AIGenomenPlayer) game.getFatherController();
        Vector3f fatherPos = fatherPlayer.getPlayer().getPosition();
        AIGenomenPlayer kidnapperPlayer = (AIGenomenPlayer) game.getKidnapperController();
        Vector3f kidnapperPos = kidnapperPlayer.getPlayer().getPosition();

        int distance = (int) fatherPos.distance(kidnapperPos);

        kidnapperScore += distance;
        fatherScore += game.getMaxDistance() - distance;

        this.setResults1(fatherPlayer, fatherScore);
        this.setResults2(kidnapperPlayer, kidnapperScore);
    }
}
