import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.awt.Point;

public class MiniMax {
    public final boolean DEBUGGING = true;
    private int recID; // todo delete
    private Board board;
    private int boardSize;
    private ArrayList<Node> roots;

    private int nodeId; // del
    private int stateId;

    MiniMax(int boardSize) {
        // Initialize the ArrayList of nodes.
        this.nodeId = 0; // del
        this.stateId = 0;
        this.board = new Board(boardSize);
        this.boardSize = boardSize;
        if (DEBUGGING) {
            System.out.println("Initial board:");
            board.printBoard();
            System.out.println("Initial Player: " + board.getCurrentPlayer() + "\n");
        }
        int [][] parentState = new int[boardSize][boardSize];
        board.copyState(parentState);
        TempNodeState initState = new TempNodeState(parentState, stateId++);
        this.roots = createNodes(true, initState);
        makeTree();
    }

    // Returns an arraylist of nodes representing the next possible moves for the current player.
    public ArrayList<Node> createNodes(boolean isMax, TempNodeState state) {
        if (DEBUGGING) System.out.println("Creating nodes:\n-");
        ArrayList<Node> nodes = new ArrayList<>();

        // Each child represents one of the next possible valid moves.
        Set<Point> valMoves = board.getValidMoves();

        if (valMoves.isEmpty()) {
            if (DEBUGGING) System.out.println("Can't make any moves, returning null\n");
            return null;
        }

        valMoves.forEach(pt -> {
            HashSet<Integer> dirs = board.getValidDirections(pt);
            Node node = new Node(state, pt, dirs, board.getCurrentPlayer(), isMax, nodeId++);
            if (DEBUGGING) {
                System.out.println("Node " + node);
                System.out.println("Directions: " + dirs);
                System.out.println("Board state:");
                node.printState();
                System.out.println("-");
            }
            nodes.add(node);
        });

        if (DEBUGGING) System.out.println();
        return nodes;
    }

    // From the parent, adds a leaf for each possible move.
    public void createLeaves(Node parent) {
        if (DEBUGGING) System.out.println("Leaves sprouting for " + parent);

        // Set the board to the parent's state and make the parent's move.
        board.setBoardState(parent.getStateBeforeMove(), parent.getPlayer());
        if (DEBUGGING) {
            System.out.println("global board state:");
            board.printBoard();
            System.out.println("parent's state:");
            parent.printState();
        }
        board.makeMove(parent.getMove());
        int [][] s = new int [boardSize][boardSize];
        board.copyState(s);
        TempNodeState childrenState = new TempNodeState(s, stateId++);
        if (DEBUGGING) {
            System.out.println("-\nglobal board after making the move:");
            board.printBoard();
            System.out.println("children's state: id " + childrenState.id);
            for (int i = 0; i < childrenState.state.length; i++) {
                for (int j = 0; j < childrenState.state.length; j++) {
                    System.out.print(childrenState.state[i][j] + " ");
                }
                System.out.println();
            }

            System.out.println("parent's state after move was made:");
            parent.printState();

            if (childrenState.id == 25) {
                System.out.println("\tChecking in on state id 0:");
                roots.get(0).printState();
            }
        }


        // Children are the opposite of their parent.
        boolean isChildMax = !parent.getIsMax();
        ArrayList<Node> children = createNodes(isChildMax, childrenState);
        parent.setChildren(children);
        if (children != null) {
            for (Node child : children) {
                createLeaves(child);
            }
        }
        // if (children != null) {
        //     parent.setChildren(children);

        //     // Recursively create leaves.
        //     if (board.getNumRemainingSpots() > 0) {
        //         for (Node child : children) {
        //             createLeaves(child);
        //         }
        //     }
        // }
    }

    public void makeTree() {
        if (this.roots != null) {
            for (Node root : roots) {
                if (DEBUGGING) System.out.println("Tree sprouting for " + root + "\n");
                createLeaves(root);
            }
        }
    }
}

class Node {
    // private int [][] stateBeforeMove;
    public TempNodeState state;
    private Point move;
    private HashSet<Integer> directions;
    private int player;
    private boolean isMaxPlayer;
    private int score;
    private List<Node> children;

    private int id; // del


    // Constructor.
    Node(TempNodeState state, Point move, HashSet<Integer> dirs, int player, boolean isMax, int id) {
        this.state = state;
        this.move = move;
        this.directions = dirs;
        this.player = player;
        this.isMaxPlayer = isMax;
        this.score = 0; // todo actually compute score
        this.children = new ArrayList<>();
        this.id = id; // del
    }

    public int [][] getStateBeforeMove() {
        return this.state.state;
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

    public int getPlayer() {
        return this.player;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    // todo delete (for debugging)
    public String toString() {
        return "(" + this.move.x + ", " + this.move.y + ") player " + this.player + " id n" + this.id;
    }

    // todo delete (for debugging)
    public void printState() {
        System.out.println("id = s" + this.state.id);
        int [][] s = this.state.state;
        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s.length; j++) {
                System.out.print(s[i][j] + " ");
            }
            System.out.println();
        }
    }

    // todo delete
    public void printTempNine(int x, int y) {
        int oldVal = this.state.state[x][y];
        this.state.state[x][y] = 9;
        printState();
        this.state.state[x][y] = oldVal;
    }
}

class TempNodeState {
    public int [][] state;
    public int id;

    public TempNodeState(int [][] s, int id) {
        this.state = s;
        this.id = id;
    }
}
