import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.awt.Point;

public class Board {
    public final boolean DEBUGGING = false;
    private int boardSize;
    private int[][] boardState;
    private int currentPlayer = 1;
    private int numRemainingSpots;

    private int[] xOffsets = {0, 1, 1,  1,  0, -1, -1, -1};
    private int[] yOffsets = {1, 1, 0, -1, -1, -1,  0,  1};
    // Amount to add to some index (mod size) to get the opposite direction.
    private final int OPP_DIRECTION = 4;
    // Maps valid moves to directions to fill in for current player.
    private HashMap<Point, HashSet<Integer>> validMoves;
    
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.boardState = new int[boardSize][boardSize];
        final int[][] startingBoard = {{0, 0, 1}, {1, 1, 1}, {0, 1, 2}, {1, 0, 2}};
        int halfway = boardSize / 2;
        for (int i = 0; i < startingBoard.length; i++) {
            int[] spot = startingBoard[i];
            this.boardState[halfway - spot[0]][halfway - spot[1]] = spot[2];
        }

        this.validMoves = null;
        this.numRemainingSpots = (boardSize * boardSize) - 4; // Board initialized with 4 spots taken.
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
    public HashSet<Point> getValidMoves() {
        if (DEBUGGING) {
            System.out.println("\nGetting valid moves for " + this.currentPlayer);
            printBoard();
        }
        this.validMoves = new HashMap<>();
        HashSet<Point> retVal = new HashSet<>();

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
                        retVal.add(foundPoint);
                        if (DEBUGGING) {
                            System.out.println("For " + foundPoint + ": added direction " + dir);
                            System.out.println("Directions: " + directions);
                            System.out.println("Sanity check " + this.validMoves.get(foundPoint));
                        }
                    }
                }  
            }
        }

        if (DEBUGGING) {
            System.out.println("Returning from getValidMoves");
            System.out.println();
        }
        return retVal;
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

        if (DEBUGGING)
        System.out.println("\nFinding move for " + currentPoint + " player " + this.currentPlayer);

        while (validIdx(currentX, currentY)) {
            if (DEBUGGING)
            tempSetBoardState(currentX, currentY, 9);

            if (!foundOppositeTile && this.boardState[currentX][currentY] == oppositePlayer) {
                if (DEBUGGING)
                System.out.println("Found opp tile");
                foundOppositeTile = true;
            }

            if (!foundEmptyTile && this.boardState[currentX][currentY] == 0) {
                if (DEBUGGING)
                System.out.println("Found 0 (move) tile");
                foundEmptyTile = true;
                break;
            }

            // This means the move can't be made.
            if (!foundMyTile && this.boardState[currentX][currentY] == this.currentPlayer) {
                if (DEBUGGING)
                System.out.println("Found my (end) tile in the path: means move is invalid");
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
            if (DEBUGGING) System.out.println("YIPPEEEEEEEEEEE valid point found");
            return new Point(currentX, currentY);
        } else {
            if (DEBUGGING) System.out.println("BAD!!!!!!!!! this one isn't valid");
            return null;
        }
    }

    // todo delete this? maybe not. for debugging
    // Returns whether the proposed move is valid for the current board.
    public boolean checkMoveValidity(Point mv) {
        if (DEBUGGING)
        System.out.println("\nChecking " + mv + "'s validity:");

        // Check for simple indexing errors.
        if (mv.x < 0 || mv.y < 0) {
            if (DEBUGGING)
            System.out.println(mv + " is not valid: index less than 0\n");
            return false;
        } else if (mv.x >= this.boardSize || mv.y >= this.boardSize) {
            if (DEBUGGING)
            System.out.println(mv + " is not valid: index greater than " + (this.boardSize - 1) + "\n");
            return false;
        }

        // Check for associated directions for the move.
        HashSet<Integer> dirs = this.validMoves.get(mv);
        if (dirs == null) {
            if (DEBUGGING)
            System.out.println(mv + " is not valid: it has no associated directions\n");
            return false;
        }

        // Check othello board for valid move positioning.
        int opponent = this.currentPlayer == 1 ? 2 : 1;
        int oppSpotsSeen = 0;
        for (Integer d : dirs) {
            int curX = mv.x;
            int curY = mv.y;
            int xIncr = xOffsets[d];
            int yIncr = yOffsets[d];

            while (true) {
                curX += xIncr;
                curY += yIncr;

                if (curX < 0 || curY < 0) {
                    if (DEBUGGING)
                    System.out.println(mv + " is not valid: to negative infinity and beyond!\n");
                    return false;
                } else if (curX >= this.boardSize || curY >= this.boardSize) {
                    if (DEBUGGING)
                    System.out.println(mv + " is not valid: to positive infinity and beyond!\n");
                    return false;
                } else if (oppSpotsSeen < 1 && this.boardState[curX][curY] == this.currentPlayer) {
                    if (DEBUGGING) {
                        System.out.println(mv + " is not valid: can't cover up my own spot:");
                        tempSetBoardState(curX, curY, 9);
                        System.out.println();
                    }
                    return false;
                } else if (this.boardState[curX][curY] != opponent && this.boardState[curX][curY] != this.currentPlayer && this.boardState[curX][curY] != 0) {
                    if (DEBUGGING) {
                        System.out.println(mv + " is not valid: there's a ghost on this spot:");
                        printBoard();
                        System.out.println();
                    }
                    return false;
                }

                oppSpotsSeen++;

                if (this.boardState[curX][curY] == this.currentPlayer) {
                    break;
                }
            }
        }

        return true;
    }

    // Modifies the board to make the valid move.
    public void makeMove(Point validMove)
    {
        // Make sure the move is in the global map of valid moves.
        if (DEBUGGING)
        System.out.println("\nMaking move " + validMove);
        if (DEBUGGING)
        tempSetBoardState(validMove.x, validMove.y, 9);
        HashSet<Integer> dirs = validMoves.get(validMove);
        if (DEBUGGING)
        System.out.println("Directions to travel: " + dirs);

        // if (dirs == null) {
        //     // todo better error handling?
        //     System.out.println("An error happened");
        //     return;
        // }

        checkMoveValidity(validMove);

        // Flip the valid move location to the current player's color.
        boardState[validMove.x][validMove.y] = this.currentPlayer;

        for (Integer direction: dirs) {
            int curX = validMove.x + xOffsets[direction];
            int curY = validMove.y + yOffsets[direction];

            // Flip tiles until we hit the our own tile.
            if (DEBUGGING)
            System.out.println("Progress:");
            while (boardState[curX][curY] != this.currentPlayer) {
                boardState[curX][curY] = this.currentPlayer;
                curX += xOffsets[direction];
                curY += yOffsets[direction];
                if (DEBUGGING)
                printBoard();
                if (DEBUGGING)
                System.out.println("================");
            }
        }

        // Toggle the current player and reset valid moves mapping.
        this.numRemainingSpots--;
        this.currentPlayer = this.currentPlayer == 1 ? 2 : 1;
        validMoves = null;

        if (DEBUGGING) {
            System.out.println("After making the move:");
            printBoard();
            System.out.println();
        }
    }

    // Check index validity. This is checked often.
    public boolean validIdx(int x, int y) {
        if (x < 0 || y < 0) return false;
        if (x >= this.boardSize || y >= this.boardSize) return false;

        return true;
    }

    // Returns the valid directions associated with a move or null if move not found.
    public HashSet<Integer> getValidDirections(Point move) {
        return validMoves.get(move);
    }

    public int getNumRemainingSpots() {
        return this.numRemainingSpots;
    }

    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    // todo delete (only for debugging)
    public void tempSetBoardState(int x, int y, int val) {
        int prevVal = this.boardState[x][y];
        this.boardState[x][y] = val;
        printBoard();
        this.boardState[x][y] = prevVal;
    }
}
