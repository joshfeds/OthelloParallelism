import java.util.ArrayList;
import java.util.HashSet;
import java.awt.Point;

public class Board {

    private int boardSize;
    private int[][] boardState;
    private int currentPlayer = 1;
    
    public Board(int boardSize) {
        this.boardSize = boardSize;
        this.boardState = new int[boardSize][boardSize];
        final int[][] startingBoard = {{0, 0, 1}, {1, 1, 1}, {0, 1, 2}, {1, 0, 2}};
        int halfway = boardSize / 2;
        for (int i = 0; i < startingBoard.length; i++) {
            int[] spot = startingBoard[i];
            this.boardState[halfway - spot[0]][halfway - spot[1]] = spot[2];
        }

    }

    public void printBoard() {
        for (int i = 0; i < this.boardState.length; i++) {
            for (int j = 0; j < this.boardState[i].length; j++) {
                System.out.print(this.boardState[i][j] + " ");
            }
            System.out.println();
        }
    }

    public HashSet<Point> getValidMoves() {
        HashSet<Point> currentSpots = new HashSet<>();
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
                        currentSpots.add(foundPoint);
                    }
                }
                
            }
        }

        return currentSpots;
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
}