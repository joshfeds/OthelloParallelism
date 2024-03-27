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

    public final int LOOKAHEAD = 2; // todo play with this value.

    MiniMax(int boardSize) throws Exception {
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
                // updateMoveScore(child);
            }
        }
    }

    public void makeTree() throws Exception {
        if (this.roots != null) {
            for (Node root : roots) {
                if (SCORE_DEBUGGING) System.out.println("Tree sprouting for " + root + "\n");
                createLeaves(root);

                // Calculate score for all leaves. 
                // NOTE!! This is just for testing the backend.
                if (SCORE_DEBUGGING) {
                    Node winner = getBestOption(root.getChildren());
                    System.out.println("The best option is: " + winner); 
                }
            }
        }
    }

    // Assigns a score to the (leaf?) node.
    // Assigns a score to the node.
    private void updateMoveScore(Node n) {
        if (n.isLeaf()) {
            // If n is a leaf node, get the score from the board class.
            board.setBoardState(n.getStateBeforeMove(), n.getPlayer());
            n.score = 0;
            n.score += board.calculateScore(n.getMove());

            if (SCORE_DEBUGGING)
                System.out.println("\tLeaf score: " + n.score);
        } else {
            // Get the score from my children.
            ArrayList<Node> myChildren = n.getChildren();

            if (n.getIsMax()) {
                // Assign n's score as the maximum of its children.
                int maxChildScore = Integer.MIN_VALUE;
                for (Node child : myChildren) {
                    if (child.score == null)
                        updateMoveScore(child);
                    
                    if (child.score > maxChildScore)
                        maxChildScore = child.score;
                }

                if (SCORE_DEBUGGING) System.out.println("\tchose max: " + maxChildScore);
                n.score = maxChildScore;
            } else {
                // Assign n's score as the minimum of its children.
                int minChildScore = Integer.MAX_VALUE;
                for (Node child : myChildren) {
                    if (child.score == null)
                        updateMoveScore(child);

                    if (child.score < minChildScore) 
                        minChildScore = child.score;
                }

                if (SCORE_DEBUGGING) System.out.println("\tchose min: " + minChildScore);
                n.score = minChildScore;
            }
        }
    }

    // Builds LOOKAHEAD levels of the gametree beneath the node n.
    public void buildLookahead(Node n, int traversalCount) {
        // Note that getBestOption, which calls this method, already ensures n is initially the bot.
        if (SCORE_DEBUGGING) System.out.println("\t\tlet's build lookahead!");

        int levelsToTraverse = LOOKAHEAD - traversalCount;

        if (levelsToTraverse > 0) {
            // If n doesn't have children, make some before traversing further.
            if (n.isLeaf())
                createLeaves(n);
            
            ArrayList<Node> children = n.getChildren();
            for (Node ch : children) 
                buildLookahead(ch, traversalCount + 1);
        }
    }

    // Returns the node from the list of options with the best score.
    // May update score values and build subtrees.
    public Node getBestOption(ArrayList<Node> options) throws Exception {
        if (!(options.get(0).getIsMax())) {
            throw new Exception("Should not be selecting move for human player.");
        }

        int max = Integer.MIN_VALUE;
        Node best = null;
        for (Node n : options) {
            if (SCORE_DEBUGGING) {
                System.out.println("\tobserving option " + n);
                n.printState();
            }
            // Make all necessary subtrees.
            buildLookahead(n, 0);
            // Update the move's score.
            updateMoveScore(n);

            if (max < n.score) {
                max = n.score;
                best = n;
            }
            if (SCORE_DEBUGGING) System.out.println("Score of " + n + ": " + n.score);
        }

        return best;
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
