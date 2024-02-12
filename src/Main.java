import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {   
        Board board = new Board(8);
        board.printBoard();

        // Make the game tree. Should not modify actual board.
        MiniMax gameTree = new MiniMax(8);

        HashMap<Point, HashSet<Integer>> validMoves = board.getValidMoves();

        for (Point point : validMoves.keySet()) {
            System.out.println(point);
        }
    }
}