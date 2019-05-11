package AI;

import util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AIGameTrainer<A, B> {

    private List<A> players;
    private List<Pair<A, A>> competition;
    private Map<A, Integer> playerScores;

    private final int nPlayers;

    public AIGameTrainer(int nPlayers) {
        this.nPlayers = nPlayers;
        players = new ArrayList<>();
        playerScores = new HashMap<>();
    }

    public void init() {
        this.createPlayers(players);
        competition = this.createCompetition(players);
    }

    public void playCompetition() {
        for (Pair<A, A> players : competition) {
            B game = this.createGame(players);
            this.playGame(game);
        }

        int bestScore = 0;
        A bestPlayer = null;
        for (A player : players) {
            int pScore = playerScores.get(player);
            if (pScore > bestScore || bestPlayer == null) {
                bestPlayer = player;
                bestScore = pScore;
            }
        }

        System.out.println("Best Score: " + bestScore);
    }

    protected void createPlayers(List<A> players) {
        for (int i = 0; i < this.nPlayers; i++) {
            A player = this.createPlayer();
            players.add(player);
            playerScores.put(player, 0);
        }
    }

    public List<A> getPlayers() {
        return players;
    }

    public List<Pair<A, A>> getCompetition() {
        return competition;
    }

    public void setResults(A player, int score) {
        int oldScore = playerScores.get(player);
        playerScores.put(player, oldScore + score);
    }

    protected abstract A createPlayer();

    protected abstract List<Pair<A, A>> createCompetition(List<A> players);

    protected abstract B createGame(Pair<A, A> players);

    protected abstract void playGame(B game);
}
