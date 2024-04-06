package pack;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class ScoreUtil {
    // Score increments
    public static final int INTERIOR_SCORE = 1;
    public static final int FRONTIER_SCORE = -1;
    public static final int CORNER_SCORE = 20;
    public static final int GOOD_BUFFER_SCORE = 1;
    public static final int BAD_BUFFER_SCORE = -3;
    public static final int EDGE_SCORE = 10;

    // Count the amount of frontier and interior pieces that will be obtained by the current player.
    // Increments for each interior and decrements for each frontier.
    public static int calculateScore(Point mv, int[][] boardState, int player, HashMap<Point, HashSet<Integer>> validMoves) {
        // todo Make that move then reverse it??
        // Initialize the result to the score of the first tile in the move.
        int result = 0;
        result += calculateSingletonScore(boardState, mv.x, mv.y, player);

        // Get the associated directions to travel.
        HashSet<Integer> dirs = validMoves.get(mv);
        for (Integer dir : dirs) {
            if (BoardGlobals.DEBUGGING) System.out.println("NEW DIRECTION!!!");
            int curX = mv.x + BoardGlobals.xOffsets[dir];
            int curY = mv.y + BoardGlobals.yOffsets[dir];

            while (boardState[curX][curY] != player) {
                if (BoardGlobals.DEBUGGING)
                    System.out.println("========\nCurrent spot is: " + curX + ", " + curY);

                result += calculateSingletonScore(boardState, curX, curY, player);

                curX += BoardGlobals.xOffsets[dir];
                curY += BoardGlobals.yOffsets[dir];
            }
        }

        return result;
    }

    // Returns the score of a single tile.
    public static int calculateSingletonScore(int[][] boardState, int row, int col, int player) {
        int res = 0;

        if (isInterior(boardState, row, col)) {
            res += INTERIOR_SCORE;
            if (BoardGlobals.DEBUGGING) System.out.println("\t(" + row + ", " + col +") is interior");
        }
        else {
            res += FRONTIER_SCORE;
            if (BoardGlobals.DEBUGGING) System.out.println("\t(" + row + ", " + col +") is frontier");
        }

        if (isCorner(row, col)) {
            res += CORNER_SCORE;
            if (BoardGlobals.DEBUGGING) System.out.println("\t(" + row + ", " + col + ") is a corner");
        } else if (isBuffer(row, col)) {
            res += bufferScore(boardState, row, col, player);
            if (BoardGlobals.DEBUGGING) System.out.println("\t(" + row + ", " + col + ") is a buffer");
        } else if (isEdge(row, col)) {
            res += EDGE_SCORE;
            if (BoardGlobals.DEBUGGING) System.out.println("\t(" + row + ", " + col +") is an edge");
        }

        return res;
    }

    // Checks surrounding tiles for empty slots.
    public static boolean isInterior(int[][] boardState, int row, int col) {
        boolean result = true;

        for (int i = 0; i < BoardGlobals.xOffsets.length; i++) {
            for (int j = 0; j < BoardGlobals.yOffsets.length; j++) {
                if (boardState[i][j] == 0)
                    result = false;
            }
        }

        return result;
    }

    public static boolean isBoardEdge(int dimension) {
        return dimension == 0 || dimension == BoardGlobals.boardSize - 1;
    }

    // Is the location a corner tile?
    public static boolean isCorner(int row, int col) {
        return (isBoardEdge(row) && isBoardEdge(col));
    }

    // Assuming corners were accounted for, checks if it's a buffer zone tile
    public static boolean isBuffer(int row, int col) {
        // Finds how close each dimension is to an edge, picking whichever edge is closer
        int rowDiff = Math.min(row, (BoardGlobals.boardSize - 1) - row);
        int colDiff = Math.min(col, (BoardGlobals.boardSize - 1) - col);
        return (rowDiff <= 1 && colDiff <= 1);
    }

    // Assumes corners and buffers were accounted for, checks if it's an edge
    public static boolean isEdge(int row, int col) {
        return (isBoardEdge(row) || isBoardEdge(col));
    }

    // Buffer zones are okay if we have the nearby corner. If we don't, they are horrible.
    public static int bufferScore(int[][] boardState, int row, int col, int player) {
        // We've guaranteed this piece is near a corner, so it's one or the other edge.
        int cornerRow = row <= 1 ? 0 : BoardGlobals.boardSize - 1;
        int cornerCol = col <= 1 ? 0 : BoardGlobals.boardSize - 1;

        return boardState[cornerRow][cornerCol] == player ? GOOD_BUFFER_SCORE : BAD_BUFFER_SCORE;
    }
}
