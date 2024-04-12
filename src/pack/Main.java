package pack;

import pack.Board;

public class Main {
    public static final double NANO_TO_SEC = 0.000000001;

    public static void main(String[] args) throws Exception {   
        Board board = new Board();
        board.printBoard();
        // try {
        //     Thread.sleep(3000);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // }

        // Make the game tree. Should not modify actual board.
        System.out.println("Constructing the game tree...");
        long start = System.nanoTime();
        // try {
        //     Thread.sleep(3000);
        // } catch (InterruptedException e) {
        //     throw new RuntimeException(e);
        // }

        MiniMax gameTree = new MiniMax();
        long end = System.nanoTime();
        
        // Begin game.
        boolean gameOver = false;
        while (!gameOver) {
            if (board.getCurrentPlayer() == 1) {
                // Choose the best move as my min.
                int minScore = Integer.MAX_VALUE;
                for (Node r : gameTree.roots) {
                    System.out.println("option has score " + r.score);
                }
            }
        }

        double runtime = (end - start) / (NANO_TO_SEC);
        System.out.println("Game tree construction finished in " + runtime + " sec.");

        // HashSet<Point> validMoves = board.getValidMoves();

        // for (Point point : validMoves) {
        //     System.out.println(point);
        // }
    }
}