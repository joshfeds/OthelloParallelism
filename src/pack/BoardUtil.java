package pack;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class BoardUtil {

    // Finds a valid spot from some spot belonging to the current player.
    private static Point findValidSpot(Point currentPoint, int xInc, int yInc, int player, int[][] boardState) {
        int currentX = currentPoint.x + xInc;
        int currentY = currentPoint.y + yInc;
        int oppositePlayer = player == 1 ? 2 : 1;
        boolean onValidMove = true;
        boolean foundOppositeTile = false;
        boolean foundEmptyTile = false;
        boolean foundMyTile = false;

        while (BoardUtil.validIdx(currentX, currentY, boardState)) {
            if (!foundOppositeTile && boardState[currentX][currentY] == oppositePlayer) {
                foundOppositeTile = true;
            }

            if (boardState[currentX][currentY] == 0) {
                foundEmptyTile = true;
                break;
            }

            // This means the move can't be made.
            if (boardState[currentX][currentY] == player) {
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

    public static HashMap<Point, HashSet<Integer>> getValidMoves(int[][] boardState, int player) {
        HashMap<Point, HashSet<Integer>> validMoves = new HashMap<>();

        for (int i = 0; i < boardState.length; i++) {
            for (int j = 0; j < boardState.length; j++) {
                if (boardState[i][j] != player) {
                    continue;
                }

                Point currentPoint = new Point(i, j);
                for (int k = 0; k < BoardGlobals.xOffsets.length; k++) {
                    Point foundPoint = BoardUtil.findValidSpot(currentPoint, BoardGlobals.xOffsets[k], BoardGlobals.yOffsets[k], player, boardState);
                    if (foundPoint != null) {
                        // Add the direction discovered for the move to the mapping.
                        HashSet<Integer> directions;
                        int dir = (k + BoardGlobals.OPP_DIRECTION) % BoardGlobals.xOffsets.length;
                        if (!validMoves.containsKey(foundPoint)) {
                            directions = new HashSet<>();
                        } else {
                            directions = validMoves.get(foundPoint);
                        }

                        directions.add(dir);
                        validMoves.put(foundPoint, directions);
                    }
                }
            }
        }

        return validMoves;
    }

    // Check index validity.
    public static boolean validIdx(int x, int y, int[][] boardState) {
        if (x < 0 || y < 0) return false;
        return x < boardState.length && y < boardState.length;
    }



    public static int[][] applyMove(int[][] oldBoardState, int player, Point validMove, HashMap<Point, HashSet<Integer>> validMoves) {
        int[][] boardState = copyState(oldBoardState);

        // Special case: If we were sent a non-move, then we pass.
        if (validMove == null) {
            return boardState;
        }

        // Make sure the move is in the global map of valid moves.
        HashSet<Integer> dirs = validMoves.get(validMove);

        // todo : should I check validity here?

        // Flip the valid move location to the current player's color.
        boardState[validMove.x][validMove.y] = player;

        for (Integer direction: dirs) {
            int curX = validMove.x + BoardGlobals.xOffsets[direction];
            int curY = validMove.y + BoardGlobals.yOffsets[direction];

            // Flip tiles until we hit our own tile.
            while (boardState[curX][curY] != player) {
                boardState[curX][curY] = player;
                curX += BoardGlobals.xOffsets[direction];
                curY += BoardGlobals.yOffsets[direction];
            }
        }

        return boardState;
    }

    // Copies the contents of the array into a new board.
    public static int[][] copyState(int [][] source) {
        int[][] dest = new int[source.length][source.length];
        for (int i = 0; i < BoardGlobals.boardSize; i++) {
            System.arraycopy(source[i], 0, dest[i], 0, BoardGlobals.boardSize);
        }
        return dest;
    }

}
