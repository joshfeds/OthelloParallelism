package pack;

import pack.Board;

import java.util.Arrays;

public class Main {
    public static final double NANO_TO_SEC = 0.000000001;

    public static final int[][] testState = {
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,1,2,0,0,0},
            {0,0,0,2,1,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0},
            {0,0,0,0,0,0,0,0}
    };

    public static final int testPlayer = 1;

    public static void main(String[] args) throws Exception {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // Make the game tree. Should not modify actual board.
        System.out.println("Constructing the game tree...");
        long start = System.nanoTime();

        MiniMax gameTree = new MiniMax();
        gameTree.board.setBoardState(testState, testPlayer);
        gameTree.roots = gameTree.createNodes(true, gameTree.board.getBoardState(),
                gameTree.board.getValidMoves(), gameTree.board.getCurrentPlayer());
        // System.out.println(Arrays.deepToString(gameTree.board.boardState));
        gameTree.board.printBoard();

        Node n = gameTree.getBestOption(gameTree.roots);
        long end = System.nanoTime();
        double runtime = (end - start) / (NANO_TO_SEC);
        System.out.println("Game tree construction finished in " + runtime + " sec.");
        System.out.println("We found the best move to be " + n.getMove());

        // HashSet<Point> validMoves = board.getValidMoves();

        // for (Point point : validMoves) {
        //     System.out.println(point);
        // }
    }
}