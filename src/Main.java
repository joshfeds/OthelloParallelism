import java.awt.Point;
import java.util.HashSet;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {    
        Board board = new Board(8);
        board.printBoard();
        HashMap<Point, HashSet<Integer>> validMoves = board.getValidMoves();

        for (Point point : validMoves.keySet()) {
            System.out.println(point);
        }
    }
}