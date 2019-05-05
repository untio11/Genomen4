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
    SHORE_T(Group.NOT_ACCESSIBLE),
    SHORE_R(Group.NOT_ACCESSIBLE),
    SHORE_D(Group.NOT_ACCESSIBLE),
    SHORE_L(Group.NOT_ACCESSIBLE),
    SHORE_RT(Group.NOT_ACCESSIBLE),
    SHORE_RD(Group.NOT_ACCESSIBLE),
    SHORE_LD(Group.NOT_ACCESSIBLE),
    SHORE_LT(Group.NOT_ACCESSIBLE),
    SHORE_TD(Group.NOT_ACCESSIBLE),
    SHORE_LR(Group.NOT_ACCESSIBLE),
    SHORE_RTD(Group.NOT_ACCESSIBLE),
    SHORE_LTD(Group.NOT_ACCESSIBLE),
    SHORE_LRD(Group.NOT_ACCESSIBLE),
    SHORE_LRT(Group.NOT_ACCESSIBLE),
    SHORE_LRTD(Group.NOT_ACCESSIBLE);

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
