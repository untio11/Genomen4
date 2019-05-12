package GameState;

import java.util.Comparator;

public class HeuristicComparator implements Comparator<Tile>
{
    @Override
    public int compare(Tile x, Tile y)
    {
        return x.getfScore() - y.getfScore();
    }
}