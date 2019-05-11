package AI;

import util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class AIGameTrainer<A> {

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
}
