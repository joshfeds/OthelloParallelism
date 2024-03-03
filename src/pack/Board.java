package pack;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.awt.Point;

public class Board {
    public final boolean DEBUGGING = true;
    private int boardSize;
    public int[][] boardState;
    private int currentPlayer = 1;

    public final int BOTPLAYER = 2;
    public final int HUMANPLAYER = 1;

    // Corresponds to various path directions.
    private int[] xOffsets = {0, 1, 1,  1,  0, -1, -1, -1};
    private int[] yOffsets = {1, 1, 0, -1, -1, -1,  0,  1};
    // Amount to add to some index (mod size) to get the opposite direction.
    private final int OPP_DIRECTION = 4;
    // Maps valid moves to directions to fill in for current player.
    private HashMap<Point, HashSet<Integer>> validMoves;
    
    public Board(int boardSize) {
        if (DEBUGGING) System.out.println("New board created.");
        this.boardSize = boardSize;
        this.boardState = new int[boardSize][boardSize];
        final int[][] startingBoard = {{0, 0, 1}, {1, 1, 1}, {0, 1, 2}, {1, 0, 2}};
        int halfway = boardSize / 2;
        for (int i = 0; i < startingBoard.length; i++) {
            int[] spot = startingBoard[i];
            this.boardState[halfway - spot[0]][halfway - spot[1]] = spot[2];
        }

        updateValidMoves();
    }

    public void printBoard() {
        for (int i = 0; i < this.boardState.length; i++) {
            for (int j = 0; j < this.boardState[i].length; j++) {
                System.out.print(this.boardState[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Updates global mapping keeping track of valid moves for the current player.
    public void updateValidMoves() {
        this.validMoves = new HashMap<>();

        // Clockwise coordinates around a spot to check for valid moves
        int[] xOffsets = {0, 1, 1,  1,  0, -1, -1, -1};
        int[] yOffsets = {1, 1, 0, -1, -1, -1,  0,  1};

        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++) {
                if (this.boardState[i][j] != this.currentPlayer) {
                    continue;
                }

                Point currentPoint = new Point(i, j);
                for (int k = 0; k < xOffsets.length; k++) {
                    Point foundPoint = findValidSpot(currentPoint, xOffsets[k], yOffsets[k]);
                    if (foundPoint != null) {
                        // Add the direction discovered for the move to the mapping.
                        HashSet<Integer> directions;
                        int dir = (k + OPP_DIRECTION) % xOffsets.length;
                        if (!this.validMoves.containsKey(foundPoint)) {
                            directions = new HashSet<>();
                        } else {
                            directions = this.validMoves.get(foundPoint);
                        }

                        directions.add(dir);
                        this.validMoves.put(foundPoint, directions);
                    }
                }  
            }
        }
    }

    // Finds a valid spot from some spot belonging to the current player.
    private Point findValidSpot(Point currentPoint, int xInc, int yInc) {
        int currentX = currentPoint.x + xInc;
        int currentY = currentPoint.y + yInc;
        int oppositePlayer = this.currentPlayer == 1 ? 2 : 1;
        boolean onValidMove = true;
        boolean foundOppositeTile = false;
        boolean foundEmptyTile = false;
        boolean foundMyTile = false;

        while (validIdx(currentX, currentY)) {
            if (!foundOppositeTile && this.boardState[currentX][currentY] == oppositePlayer) {
                foundOppositeTile = true;
            }

            if (!foundEmptyTile && this.boardState[currentX][currentY] == 0) {
                foundEmptyTile = true;
                break;
            }

            // This means the move can't be made.
            if (!foundMyTile && this.boardState[currentX][currentY] == this.currentPlayer) {
                foundMyTile = true;
                break;
            }

            currentX += xInc;
            currentY += yInc;
            onValidMove = false;
        }

        // If we've seen at least one opposite tile, a single empty
        // tile, and none of my own tiles, the move is valid.
        if (foundOppositeTile && foundEmptyTile && !foundMyTile) {
            return new Point(currentX, currentY);
        } else {
            return null;
        }
    }

    // Modifies the board to make the valid move.
    public void makeMove(Point validMove)
    {
        // Make sure the move is in the global map of valid moves.
        HashSet<Integer> dirs = validMoves.get(validMove);

        // todo : should I check validity here?

        // Flip the valid move location to the current player's color.
        boardState[validMove.x][validMove.y] = this.currentPlayer;

        for (Integer direction: dirs) {
            int curX = validMove.x + xOffsets[direction];
            int curY = validMove.y + yOffsets[direction];

            // Flip tiles until we hit the our own tile.
            while (boardState[curX][curY] != this.currentPlayer) {
                boardState[curX][curY] = this.currentPlayer;
                curX += xOffsets[direction];
                curY += yOffsets[direction];
            }
        }

        // Toggle the current player and reset valid moves mapping.
        this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;

        updateValidMoves();
    }

    // Check index validity. todo delete?
    public boolean validIdx(int x, int y) {
        if (x < 0 || y < 0) return false;
        if (x >= this.boardSize || y >= this.boardSize) return false;

        return true;
    }

    // Copies the contents of the current board state into the array.
    // For node creation in the minimax game tree.
    public void copyState(int [][] copyTo) {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++)
                copyTo[i][j] = this.boardState[i][j];
        }
    }

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
        for (int i = 0; i < this.boardSize; i++) {
            for (int j = 0; j < this.boardSize; j++)
                this.boardState[i][j] = newState[i][j];
        }
        this.currentPlayer = curPlayer;
        updateValidMoves();
    }

    // Returns the set of valid moves the current player can make.
    public Set<Point> getValidMoves() {
        return this.validMoves.keySet();
    }

    // Returns directions I neet to travel to make the move.
    public HashSet<Integer> getDirections(Point move) {
        return this.validMoves.get(move);
    }
}
