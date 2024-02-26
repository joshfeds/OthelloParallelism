import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.awt.Point;

public class MiniMax {
    private ArrayList<Node> roots;

    MiniMax(int boardSize) {
        // Initialize the ArrayList of nodes.
        Board board = new Board(boardSize);
        this.roots = createNodes(board, true);
        makeTree(board);
    }

    // Returns an arraylist of nodes representing the next possible moves for the current player.
    public ArrayList<Node> createNodes(Board board, boolean isMax) {
        ArrayList<Node> nodes = new ArrayList<>();

        // Each child represents one of the next possible valid moves.
        HashSet<Point> valMoves = board.getValidMoves();
        System.out.println("This group's valid moves for " + board.getCurrentPlayer() + ":");
        valMoves.forEach(pt -> {
            System.out.println(pt);
            board.tempSetBoardState(pt.x, pt.y, 9);
            HashSet<Integer> dirs = board.getValidDirections(pt);
            Node newNode = new Node(pt, dirs, board.getCurrentPlayer(), isMax);
            nodes.add(newNode);
        });

        System.out.println();
        return nodes;
    }

    // From the parent, adds a leaf for each possible move.
    public void createLeaves(Node parent, Board board) {
        // Make the move specified in the parent node.
        board.makeMove(parent.getMove());
        // board.printBoard();

        // Children are the opposite of their parent.
        boolean isChildMax = !parent.getIsMax();
        ArrayList<Node> children = createNodes(board, isChildMax);
        parent.setChildren(children);

        // Recursively create leaves.
        if (board.getNumRemainingSpots() > 0) {
            for (Node child : children) {
                createLeaves(child, board);
            }
        }
    }

    public void makeTree(Board board) {
        if (this.roots != null) {
            for (Node root : roots) {
                createLeaves(root, board);
            }
        }
    }
}

class Node {
    private Point move;
    private HashSet<Integer> directions;
    private int player;
    private boolean isMaxPlayer;
    private int score;
    private List<Node> children;

    // Constructor.
    Node(Point move, HashSet<Integer> dirs, int player, boolean isMax) {
        this.move = move;
        this.directions = dirs;
        this.player = player;
        this.isMaxPlayer = isMax;
        this.score = 0; // todo actually compute score
        this.children = new ArrayList<>();
    }

    // Getters and setters.
    public boolean getIsMax() {
        return this.isMaxPlayer;
    }

    public void setIsMax(boolean isMax) {
        this.isMaxPlayer = isMax;
    }

    public Point getMove() {
        return this.move;
    }

    public HashSet<Integer> getDirections() {
        return this.directions;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }
}
