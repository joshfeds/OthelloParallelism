// Class to hold debugging functions

public class Debug {

    // Returns whether the proposed move is valid for the current board.
    public boolean checkMoveValidity(Point mv, Board board) {
        System.out.println("\nChecking " + mv + "'s validity:");

        // Check for simple indexing errors.
        if (mv.x < 0 || mv.y < 0) {
            System.out.println(mv + " is not valid: index less than 0\n");
            return false;
        } else if (mv.x >= this.boardSize || mv.y >= this.boardSize) {
            System.out.println(mv + " is not valid: index greater than " + (this.boardSize - 1) + "\n");
            return false;
        }

        // Check for associated directions for the move.
        HashSet<Integer> dirs = this.validMoves.get(mv);
        if (dirs == null) {
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
                    System.out.println(mv + " is not valid: exited the board while travelling");
                    return false;
                } else if (curX >= this.boardSize || curY >= this.boardSize) {
                    System.out.println(mv + " is not valid: exited the board while travelling");
                    return false;
                } else if (oppSpotsSeen < 1 && this.boardState[curX][curY] == this.currentPlayer) {
                    System.out.println(mv + " is not valid: haven't seen the opposing player on the path");
                    return false;
                } else if (this.boardState[curX][curY] != opponent && this.boardState[curX][curY] != this.currentPlayer) {
                    System.out.println(mv + " is not valid: invalid path containing 0");
                    return false;
                }

                oppSpotsSeen++;

                if (this.boardState[curX][curY] == this.currentPlayer) {
                    break;
                }
            }
        }

        System.out.println("all good");
        return true;
    }

    // Prints the board state with val in the specified spot (for seeing where a move is easily).
    public void tempSetBoardState(int x, int y, int val, Board board) {
        int [][] state = board.getBoardState();
        int prevVal = state[x][y];
        state[x][y] = val;
        board.printBoard();
        state[x][y] = prevVal;
    }
}