import java.util.Set;
import java.util.HashSet;
import java.awt.Point;

// Class to hold debugging functions

/*import java.awt.*;
import java.util.HashSet;

public class Debug {

    // Returns whether the proposed move is valid for the current board.
    public boolean checkMoveValidity(Point mv, pack.Board board, int xIncr, int yIncr) {
        System.out.println("\nChecking " + mv + "'s validity:");

        int currentPlayer = board.getCurrentPlayer();
        int opponent = currentPlayer == 1 ? 2 : 1;
        int oppSpotsSeen = 0;
        Set<Integer> dirs = board.getDirections(mv);
        int [][] boardState = board.getBoardState();
        int boardSize = boardState.length;

        // Check for simple indexing errors.
        if (mv.x < 0 || mv.y < 0) {
            System.out.println(mv + " is not valid: index less than 0\n");
            return false;
        } else if (mv.x >= boardSize || mv.y >= boardSize) {
            System.out.println(mv + " is not valid: index greater than " + (boardSize - 1) + "\n");
            return false;
        }

        // Check for associated directions for the move.
        if (dirs == null) {
            System.out.println(mv + " is not valid: it has no associated directions\n");
            return false;
        }

        // Check othello board for valid move positioning.
        for (Integer d : dirs) {
            int curX = mv.x;
            int curY = mv.y;

            while (true) {
                curX += xIncr;
                curY += yIncr;

                if (curX < 0 || curY < 0) {
                    System.out.println(mv + " is not valid: exited the board while travelling");
                    return false;
                } else if (curX >= boardSize || curY >= boardSize) {
                    System.out.println(mv + " is not valid: exited the board while travelling");
                    return false;
                } else if (oppSpotsSeen < 1 && boardState[curX][curY] == currentPlayer) {
                    System.out.println(mv + " is not valid: haven't seen the opposing player on the path");
                    return false;
                } else if (boardState[curX][curY] != opponent && boardState[curX][curY] != currentPlayer) {
                    System.out.println(mv + " is not valid: invalid path containing 0");
                    return false;
                }

                oppSpotsSeen++;

                if (boardState[curX][curY] == currentPlayer) {
                    break;
                }
            }
        }

        System.out.println("all good");
        return true;
    }

    // Prints the board state with val in the specified spot (for seeing where a move is easily).
    public void tempSetBoardState(int x, int y, int val, pack.Board board) {
        int [][] state = board.getBoardState();
        int prevVal = state[x][y];
        state[x][y] = val;
        board.printBoard();
        state[x][y] = prevVal;
    }
  }
 */
