package pack;

import java.util.*;
import java.awt.Point;
import java.util.concurrent.threadPool;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

public class MiniMax {
    public final boolean DEBUGGING = true;
    public final boolean SCORE_DEBUGGING = false;
    public Board board;
    public ArrayList<Node> roots;

    public final int NUM_THREADS = 8; // todo mess with this value??
    private threadPool threadPool;

    public final int LOOKAHEAD = 2; // todo play with this value.

    MiniMax() throws Exception {
        // Initialize the ArrayList of root nodes and thread pool.
        this.board = new Board();
        this.threadPool = Executors.newFixedThreadPool(NUM_THREADS);

        int [][] parentState = new int[BoardGlobals.boardSize][BoardGlobals.boardSize];
        parentState = BoardUtil.copyState(board.getBoardState());
        this.roots = createNodes(false, parentState,
                BoardUtil.getValidMoves(parentState, board.getCurrentPlayer()), board.getCurrentPlayer());
        makeTree();
    }

    // Returns an arraylist of nodes representing the next possible moves for the current player.
    public ArrayList<Node> createNodes(boolean isMax, int [][] state, HashMap<Point, HashSet<Integer>> validMoves, int player) {
        if (DEBUGGING) System.out.println("Creating nodes:\n-");
        ArrayList<Node> nodes = new ArrayList<>();


        if (validMoves.isEmpty()) {
            if (BoardGlobals.DEBUGGING) System.out.println("Can't make any moves! Passing for this player's round.\n");
            nodes.add(new Node(state, null, player, isMax));
            return nodes;
        }

        validMoves.keySet().forEach(pt -> {
            Node node = new Node(state, pt, player, isMax);
            if (BoardGlobals.DEBUGGING) {
                System.out.println("pack.Node " + node);
                System.out.println("pack.Board state:");
                node.printState();
                System.out.println("-");
            }
            nodes.add(node);
        });

        if (DEBUGGING) System.out.println();
        return nodes;
    }

    // From the parent, adds a leaf for each possible move we happen to jive with.
    public void createLeaves(Node parent) {
        if (DEBUGGING) System.out.println("Leaves sprouting for " + parent);

        // Make the parent's move.
        if (DEBUGGING) {
            System.out.println("global board state:");
            board.printBoard();
            System.out.println("parent's state:");
            parent.printState();
        }
        int[][] childrenState = BoardUtil.applyMove(parent.getStateBeforeMove(), parent.getPlayer(), parent.getMove(),
                BoardUtil.getValidMoves(parent.getStateBeforeMove(), parent.getPlayer()));
        if (DEBUGGING) {
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
        ArrayList<Node> children = createNodes(isChildMax, childrenState,
                BoardUtil.getValidMoves(childrenState,
                        parent.getPlayer() == 1 ? 2 : 1), parent.getPlayer() == 1 ? 2 : 1);
        parent.setChildren(children);
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
            n.score = 0;
            n.score += ScoreUtil.calculateScore(n.getMove(), n.getStateBeforeMove(), n.getPlayer(),
                    BoardUtil.getValidMoves(n.getStateBeforeMove(), n.getPlayer()));

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

                    if (BoardGlobals.isPruningEnabled) {
                        if (maxChildScore > n.getAlpha())
                            n.setAlpha(maxChildScore);

                        if (n.getAlpha() >= n.getBeta()) {
                            if (SCORE_DEBUGGING) System.out.println("PRUNED!");
                            break;
                        }
                    }
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

                    if (BoardGlobals.isPruningEnabled) {
                        if (minChildScore < n.getBeta())
                            n.setBeta(minChildScore);

                        if (n.getAlpha() >= n.getBeta()) {
                            if (SCORE_DEBUGGING) System.out.println("PRUNED!");
                            break;
                        }
                    }
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
    public Node getBestOption(ArrayList<Node> options) {

        int max = Integer.MIN_VALUE;
        Node best = null;
        CountDownLatch lookaheadLatch = new CountDownLatch(options.size());

        for (Node n : options) {
            if (SCORE_DEBUGGING) {
                System.out.println("\tobserving option " + n);
                n.printState();
            }

                
            Runnable buildLookaheadTask = () -> {
                buildLookahead(n, 0);
                lookaheadLatch.countDown();
            };

            // Make all necessary subtrees.
            threadPool.submit(buildLookaheadTask);
            try {
                lookaheadLatch.await();
            } catch (InterruptedException e) {
                System.out.println(e);
            }

            // Update the move's score.
            Runnable updateScoreTask = () -> {
                updateMoveScore(n);
            };
            threadPool.submit(updateScoreTask);

            if (max < n.score) {
                max = n.score;
                best = n;
            }
            if (SCORE_DEBUGGING) System.out.println("Score of " + n + ": " + n.score);
        }

        return best;
    }

    public void killThreads() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                threadPool.shutdownNow();
            } 
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
        }
    }
}

class Node {
    private int [][] stateBeforeMove;
    private Point move;
    private int player;
    private boolean isMaxPlayer;
    public Integer score;
    private ArrayList<Node> children;
    private int alpha; // chad
    private int beta; // male

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

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setBeta(int beta) {
        this.beta = beta;
    }

    public int getBeta() {
        return this.beta;
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
