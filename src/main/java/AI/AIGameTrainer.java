package AI;

import util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AIGameTrainer<A, B> {

    private List<A> players;
    private List<Pair<A, A>> competition;

    private final int nPlayers;

    public AIGameTrainer(int nPlayers) {
        this.nPlayers = nPlayers;
        players = new ArrayList<>();
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
    }

    protected void createPlayers(List<A> players) {
        for (int i = 0; i < this.nPlayers; i++) {
            players.add(this.createPlayer());
        }
    }

    public List<A> getPlayers() {
        return players;
    }

    public List<Pair<A, A>> getCompetition() {
        return competition;
    }

    protected abstract A createPlayer();

    protected abstract List<Pair<A, A>> createCompetition(List<A> players);

    protected abstract B createGame(Pair<A, A> players);

    protected abstract void playGame(B game);
}
