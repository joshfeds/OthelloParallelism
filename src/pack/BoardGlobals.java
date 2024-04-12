package pack;

public class BoardGlobals {
    public static final boolean DEBUGGING = false;

    // Alpha Beta pruning can be enabled and disabled.
    public static final boolean isPruningEnabled = true;

    // Board size of the game as of running it.
    public static final int boardSize = 8;

    // Corresponds to various path directions.
    public static final int[] xOffsets = {0, 1, 1,  1,  0, -1, -1, -1};
    public static final int[] yOffsets = {1, 1, 0, -1, -1, -1,  0,  1};

    // Amount to add to some index (mod size) to get the opposite direction.
    public static final int OPP_DIRECTION = 4;
    // Maps valid moves to directions to fill in for current player.
}
