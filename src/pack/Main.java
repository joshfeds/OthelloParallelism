package pack;

import pack.Board;

import java.util.Arrays;

public class Main {
    public static final double NANO_TO_SEC = 0.000000001;

    // public static final int[][] testState = {
    //         {0,0,0,0,0,0,0,0},
    //         {0,0,0,0,0,0,0,0},
    //         {0,0,0,0,0,0,0,0},
    //         {0,0,0,1,2,0,0,0},
    //         {0,0,0,2,1,0,0,0},
    //         {0,0,0,0,0,0,0,0},
    //         {0,0,0,0,0,0,0,0},
    //         {0,0,0,0,0,0,0,0}
    // };

    public static final int[][] testState = {
        {0,0,0,0,0,0,0,0},
        {0,0,2,0,0,0,0,0},
        {0,0,0,2,1,0,0,0},
        {0,0,0,1,1,0,0,0},
        {0,0,2,2,1,2,0,0},
        {0,0,0,0,2,1,0,0},
        {0,0,0,2,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };

    public static final int testPlayer = 1;

    public static void main(String[] args) throws Exception {

        // Make the game tree. Should not modify actual board.
        System.out.println("Constructing the game tree...");

        MiniMax gameTree = new MiniMax();
        gameTree.board.setBoardState(testState, testPlayer);
        gameTree.roots = gameTree.createNodes(true, gameTree.board.getBoardState(),
                gameTree.board.getValidMoves(), gameTree.board.getCurrentPlayer());
        // System.out.println(Arrays.deepToString(gameTree.board.boardState));
        gameTree.board.printBoard();

        long start = System.nanoTime();
        Node n = gameTree.getBestOption(gameTree.roots);
        long end = System.nanoTime();
        gameTree.killThreads();
        System.out.println("Sample board evaluation finished in " + (end - start) + " nanoseconds.");
        System.out.println("We found the best move to be " + n.getMove());
    }
}