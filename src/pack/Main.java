package pack;

import pack.Board;

import java.util.Arrays;

public class Main {
    public static final double NANO_TO_SEC = 0.000000001;
    public static final int NANO_TO_MILLIS = 1000000;
    public static final int TEST_RUNS = 100;

    public static final int[][] testState1 = {
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,1,2,0,0,0},
        {0,0,0,2,1,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };

    public static final int[][] testState2 = {
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,2,0},
        {0,0,0,1,0,1,2,0},
        {0,0,2,2,2,2,2,0},
        {0,0,0,1,1,1,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };

    public static final int[][] testState3 = {
        {0,0,0,0,0,0,0,0},
        {0,0,2,0,0,0,0,0},
        {0,0,0,2,1,0,0,0},
        {0,0,0,1,1,0,0,0},
        {0,0,2,2,1,2,0,0},
        {0,0,0,0,2,1,0,0},
        {0,0,0,2,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };

    public static final int[][] testState4 = {
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,2,0},
        {0,1,2,2,2,2,2,0},
        {0,2,2,2,1,2,2,0},
        {0,0,0,1,1,1,0,0},
        {0,0,0,2,1,0,0,0},
        {0,0,0,0,0,0,0,0},
        {0,0,0,0,0,0,0,0}
    };

    public static final int testPlayer = 1;

    public static final int [] threadAmts = {3, 4, 8, 12, 16, 20, 24, 28, 32, 36, 40};

    public static void main(String[] args) throws Exception {

        // Make the game tree. Should not modify actual board.
        // System.out.println("Constructing the game tree...");
        double avg = 0;

        for (int i = 0; i < threadAmts.length; i++) {
            System.out.print(threadAmts[i] + " threads avg runtime: ");
            for (int j = 1; j <= TEST_RUNS; j++) {
                MiniMax gameTree = new MiniMax();
                gameTree.numThreads = threadAmts[i];
                gameTree.board.setBoardState(testState4, testPlayer);
                gameTree.roots = gameTree.createNodes(true, gameTree.board.getBoardState(),
                        gameTree.board.getValidMoves(), gameTree.board.getCurrentPlayer());
                // System.out.println(Arrays.deepToString(gameTree.board.boardState));
                // gameTree.board.printBoard();
        
                long start = System.nanoTime();
                Node n = gameTree.getBestOption(gameTree.roots);
                long end = System.nanoTime();
                gameTree.killThreads();
                // System.out.println("\tSample board evaluation finished in " + (end - start) + " nanoseconds.");
                // System.out.println("We found the best move to be " + n.getMove());
                
                avg += ((end - start));
            }
    
            avg = avg / TEST_RUNS;
            System.out.println(String.format("%.12f", avg) + " ns");        
        }
    }
}