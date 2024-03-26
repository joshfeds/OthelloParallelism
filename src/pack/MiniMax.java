package pack;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;
import java.awt.Point;

public class MiniMax {
    public final boolean DEBUGGING = false;
    public final boolean SCORE_DEBUGGING = true;
    private Board board;
    private int boardSize;
    private ArrayList<Node> roots;

    public final int LOOKAHEAD = 10; // todo play with this value.

    MiniMax(int boardSize) {
        // Initialize the ArrayList of root nodes.
        this.board = new Board(boardSize);
        this.boardSize = boardSize;
        int [][] parentState = new int[boardSize][boardSize];
        board.copyState(parentState);
        this.roots = createNodes(false, parentState);
        makeTree();
    }

    // Returns an arraylist of nodes representing the next possible moves for the current player.
    public ArrayList<Node> createNodes(boolean isMax, int [][] state) {
        if (DEBUGGING) System.out.println("Creating nodes:\n-");
        ArrayList<Node> nodes = new ArrayList<>();

        // Each child represents one of the next possible valid moves.
        Set<Point> valMoves = board.getValidMoves();

        if (valMoves.isEmpty()) {
            if (DEBUGGING) System.out.println("Can't make any moves, returning null\n");
            return null;
        }

        valMoves.forEach(pt -> {
            HashSet<Integer> dirs = board.getDirections(pt);
            Node node = new Node(state, pt, board.getCurrentPlayer(), isMax);
            if (DEBUGGING) {
                System.out.println("pack.Node " + node);
                System.out.println("Directions: " + board.getDirections(pt));
                System.out.println("pack.Board state:");
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
        int [][] childrenState = new int [boardSize][boardSize];
        board.copyState(childrenState);
        if (DEBUGGING) {
            System.out.println("-\nglobal board after making the move:");
            board.printBoard();
            System.out.println("children's state:");
            for (int i = 0; i < childrenState.length; i++) {
                for (int j = 0; j < childrenState.length; j++) {
                    System.out.print(childrenState[i][j] + " ");
                }
                System.out.println();
            }
        }

        // Children are the opposite of their parent.
        boolean isChildMax = !parent.getIsMax();
        ArrayList<Node> children = createNodes(isChildMax, childrenState);
        parent.setChildren(children);
        if (children != null) {
            for (Node child : children) {
                // createLeaves(child);
                updateMoveScore(child);
            }
        }
    }

    public void makeTree() {
        if (this.roots != null) {
            for (Node root : roots) {
                if (DEBUGGING) System.out.println("Tree sprouting for " + root + "\n");
                createLeaves(root);
            }
        }
    }

    // Assigns a score to the (leaf?) node.
    // TODO implement lookahead
    private void updateMoveScore(Node n) {
        if (SCORE_DEBUGGING) 
            System.out.println("Calculating score for " + n);

        // If n is a leaf node, do what I did below
        if (n.isLeaf()) {
            n.score = 0;
            n.score += board.calculateScore(n.getMove());

            if (SCORE_DEBUGGING)
                System.out.println("Score after counting frontiers and interiors: " + n.score);
        } else {
            ArrayList<Node> myChildren = n.getChildren();

            // todo dry violation?? :(
            if (n.getIsMax()) {
                int maxChildScore = Integer.MIN_VALUE;
                for (Node child : myChildren) {
                    if (child.score == null)
                        updateMoveScore(child);
                    
                    if (child.score > maxChildScore)
                        maxChildScore = child.score;
                }

                n.score = maxChildScore;
            } else {
                int minChildScore = Integer.MAX_VALUE;
                for (Node child : myChildren) {
                    if (child.score == null)
                        updateMoveScore(child);

                    if (child.score < minChildScore) 
                        minChildScore = child.score;
                }

                n.score = minChildScore;
            }
        }
    }

    // TODO
    // Function that returns node out of some list of nodes with the best score
    public Node getBestOption(ArrayList<Node> options) throws Exception {
        if (!(options.get(0).getIsMax())) {
            throw new Exception("Should not be selecting move for human player.");
        }

        for (Node n : options) {
            // Make all necessary subtrees.
            // Update the move's score.
            updateMoveScore(n);
        }

        return null; // todo delete
    }
}

class Node {
    private int [][] stateBeforeMove;
    private Point move;
    private int player;
    private boolean isMaxPlayer;
    public Integer score;
    private ArrayList<Node> children;

    // Constructor.
    Node(int [][] state, Point move, int player, boolean isMax) {
        this.stateBeforeMove = state;
        this.move = move;
        this.player = player;
        this.isMaxPlayer = isMax;
        this.score = null; // Assign a value on calculation.
        // this.children = new ArrayList<>();
        this.children = null;
    }

    public boolean isLeaf() {
        return (children == null);
    }

    // Getters and setters.

    public int [][] getStateBeforeMove() {
        return this.stateBeforeMove;
    }

    public Point getMove() {
        return this.move;
    }

    public int getPlayer() {
        return this.player;
    }

    public boolean getIsMax() {
        return this.isMaxPlayer;
    }

    public void setChildren(ArrayList<Node> children) {
        this.children = children;
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    // Functions for debugging output:

    public String toString() {
        return "(" + this.move.x + ", " + this.move.y + ") player " + this.player;
    }

    public void printState() {
        System.out.println("id = s");
        for (int i = 0; i < this.stateBeforeMove.length; i++) {
            for (int j = 0; j < this.stateBeforeMove.length; j++) {
                System.out.print(this.stateBeforeMove[i][j] + " ");
            }
            System.out.println();
        }
    }
}
