import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.awt.Point;

public class Board {

    private int boardSize;
    private int[][] boardState;
    private int currentPlayer = 1;
    private int numRemainingSpots;

    private int[] xOffsets = {0, 1, 1,  1,  0, -1, -1, -1};
    private int[] yOffsets = {1, 1, 0, -1, -1, -1,  0,  1};
    // Amount to add to some index (mod size) to get the opposite direction.
    private final int OPP_DIRECTION = 4;
    
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.boardState = new int[boardSize][boardSize];
        final int[][] startingBoard = {{0, 0, 1}, {1, 1, 1}, {0, 1, 2}, {1, 0, 2}};
        int halfway = boardSize / 2;
        for (int i = 0; i < startingBoard.length; i++) {
            int[] spot = startingBoard[i];
            this.boardState[halfway - spot[0]][halfway - spot[1]] = spot[2];
        }

        this.numRemainingSpots = boardSize - 4; // Board initialized with 4 spots taken.
    }

    public void printBoard() {
        for (int i = 0; i < this.boardState.length; i++) {
            for (int j = 0; j < this.boardState[i].length; j++) {
                System.out.print(this.boardState[i][j] + " ");
            }
            System.out.println();
        }
    }

    // Return a mapping of valid spots with the directions that can be filled from them.
    // Directions are in the form of an index into the offsets arrays.
    public HashMap<Point, HashSet<Integer>> getValidMoves() {
        HashMap<Point, HashSet<Integer>> moves = new HashMap<>();

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
                        if (moves.containsKey(foundPoint)) {
                            // Add the opposite direction to the map.
                            moves.get(foundPoint).add((k + OPP_DIRECTION) % xOffsets.length);
                        } else {
                            HashSet<Integer> direction = new HashSet<>();
                            direction.add((k + OPP_DIRECTION) % xOffsets.length); // Opposite direction.
                            moves.put(foundPoint, direction);
                        }
                    }
                }  
            }
        }

        return moves;
    }

    private Point findValidSpot(Point currentPoint, int xInc, int yInc) {
        int currentX = currentPoint.x + xInc;
        int currentY = currentPoint.y + yInc;
        int oppositePlayer = this.currentPlayer == 1 ? 2 : 1;
        boolean foundOppositeTile = false;
        boolean foundEmptyTile = false;

        while ((currentX < boardSize && currentX > 0) && (currentY < boardSize && currentY > 0)) {
            if (!foundOppositeTile && this.boardState[currentX][currentY] == oppositePlayer) {
                foundOppositeTile = true;
            }

            if (!foundEmptyTile && this.boardState[currentX][currentY] == 0) {
                foundEmptyTile = true;
                break;
            }

            currentX += xInc;
            currentY += yInc;
        }

        if (foundEmptyTile && foundOppositeTile) {
            return new Point(currentX, currentY);
        } else {
            return null;
        }
    }

    // Modifies the board to make the valid move.
    public void makeMove(Point validMove, HashSet<Integer> directions)
    {
        // Flip the valid move location to the current player's color.
        boardState[validMove.x][validMove.y] = this.currentPlayer;

        for (Integer direction: directions) {
            int curX = validMove.x + xOffsets[direction];
            int curY = validMove.y + yOffsets[direction];

            // Flip tiles until we hit the our own tile.
            while (boardState[curX][curY] != this.currentPlayer) {
                boardState[curX][curY] = this.currentPlayer;
                curX += xOffsets[direction];
                curY += yOffsets[direction];
            }
        }

        // Toggle the current player.
        this.numRemainingSpots--;
        this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;
    }

    public int getNumRemainingSpots() {
        return this.numRemainingSpots;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }
}
