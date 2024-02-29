import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;
import java.math.BigDecimal;

public class Main {
    public static final double NANO_TO_SEC = 0.000000001;

    public static void main(String[] args) {   
        Board board = new Board(8);
        // board.printBoard();

        // Make the game tree. Should not modify actual board.
        System.out.println("Constructing the game tree...");
        long start = System.nanoTime();
        MiniMax gameTree = new MiniMax(8);
        long end = System.nanoTime();
        double runtime = (end - start) / (NANO_TO_SEC);
        System.out.println("Game tree construction finished in " + runtime + " sec.");

        // HashSet<Point> validMoves = board.getValidMoves();

        // for (Point point : validMoves) {
        //     System.out.println(point);
        // }
    }
}