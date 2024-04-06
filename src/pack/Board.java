package pack;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.awt.Point;

public class Board {

    public int[][] boardState;
    private int currentPlayer = 1;
    private int boardSize;
    public final int BOTPLAYER = 2;
    public final int HUMANPLAYER = 1;

    private HashMap<Point, HashSet<Integer>> validMoves;


    
    public Board() {
        if (BoardGlobals.DEBUGGING) System.out.println("New board created.");
        this.boardState = new int[BoardGlobals.boardSize][BoardGlobals.boardSize];
        final int[][] startingBoard = {{0, 0, 1}, {1, 1, 1}, {0, 1, 2}, {1, 0, 2}};
        int halfway = BoardGlobals.boardSize / 2;
        for (int[] spot : startingBoard) {
            this.boardState[halfway - spot[0]][halfway - spot[1]] = spot[2];
        }

        updateValidMoves();
    }

    public void printBoard() {
        for (int[] row : this.boardState) {
            for (int spot : row) {
                System.out.print(spot + " ");
            }
            System.out.println();
        }
    }

    // Updates global mapping keeping track of valid moves for the current player.
    public void updateValidMoves() {
        this.validMoves = BoardUtil.getValidMoves(this.boardState, this.currentPlayer);
    }

    // Modifies the board to make the valid move.
    public void makeMove(Point validMove)
    {
        this.boardState = BoardUtil.applyMove(this.boardState, this.currentPlayer, validMove, this.validMoves);

        // Toggle the current player and reset valid moves mapping.
        this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;

        updateValidMoves();
    }

    // Copies the contents of the current board state into the array.
    // For node creation in the minimax game tree.
    public void copyState(int [][] copyTo) {
        for (int i = 0; i < BoardGlobals.boardSize; i++) {
            for (int j = 0; j < BoardGlobals.boardSize; j++)
                copyTo[i][j] = this.boardState[i][j];
        }
    }

    // Methods for score calculation:



    // Getters and setters:

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    public int [][] getBoardState() {
        return this.boardState;
    }

    // Updates the board with the given information.
    public void setBoardState(int [][] newState, int curPlayer) {
        // Copy the information into the board's state.
        for (int i = 0; i < BoardGlobals.boardSize; i++) {
            for (int j = 0; j < BoardGlobals.boardSize; j++)
                this.boardState[i][j] = newState[i][j];
        }
        this.currentPlayer = curPlayer;
        updateValidMoves();
    }

    // Returns the set of valid moves the current player can make.
    public HashMap<Point, HashSet<Integer>> getValidMoves() {
        return this.validMoves;
    }

    public Set<Point> getValidMoveset() {
        return this.validMoves.keySet();
    }

    // Returns valid directions to travel when making the move.
    public HashSet<Integer> getValidDirections(Point mv) {
        return this.validMoves.get(mv);
    }

    // Returns directions I neet to travel to make the move.
    public HashSet<Integer> getDirections(Point move) {
        return this.validMoves.get(move);
    }
}
