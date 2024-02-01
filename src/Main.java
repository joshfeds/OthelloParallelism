import java.awt.Point;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {    
        Board board = new Board(8);
        board.printBoard();
        HashSet<Point> validPoints = board.getValidMoves();
        for (Point point : validPoints) {
            System.out.println(point);
        }
    }
}