package GameState;

/**
 * TileTypes for the worldmap. Note the Shore has a lot of different types, to make it easier for rendering. Also note
 * the division in groups. You can easily check this way whether a tile is accessible. The SHORE types define at which
 * sides of the tile the shore is. T = top, R = right, D = Down and L = left. All combinations exist and are always
 * from LEFT to RIGHT, TOP to BOTTOM.
 */

public enum TileType {
    GRASS(Group.ACCESSIBLE),
    SAND(Group.ACCESSIBLE),
    TREE(Group.NOT_ACCESSIBLE),
    WATER(Group.NOT_ACCESSIBLE),
    SHORE_N(Group.NOT_ACCESSIBLE),
    SHORE_E(Group.NOT_ACCESSIBLE),
    SHORE_S(Group.NOT_ACCESSIBLE),
    SHORE_W(Group.NOT_ACCESSIBLE),
    SHORE_NE(Group.NOT_ACCESSIBLE),
    SHORE_ES(Group.NOT_ACCESSIBLE),
    SHORE_SW(Group.NOT_ACCESSIBLE),
    SHORE_NW(Group.NOT_ACCESSIBLE),
    SHORE_NS(Group.NOT_ACCESSIBLE),
    SHORE_EW(Group.NOT_ACCESSIBLE),
    SHORE_NES(Group.NOT_ACCESSIBLE),
    SHORE_NSW(Group.NOT_ACCESSIBLE),
    SHORE_ESW(Group.NOT_ACCESSIBLE),
    SHORE_NEW(Group.NOT_ACCESSIBLE),
    SHORE_NESW(Group.NOT_ACCESSIBLE);

    private Group group;

    TileType(Group group) {
        this.group = group;
    }

    public boolean isAccessible(TileType t) {
        return t.group == Group.ACCESSIBLE;
    }

    public enum Group {
        ACCESSIBLE,
        NOT_ACCESSIBLE
    }
}
