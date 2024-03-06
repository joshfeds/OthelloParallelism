// NOTE: If you are reading this in the repo, this may not compile yet, as certain JavaFX modules must be set in the project first
package pack;

// no wildcard imports :[
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.HashSet;
import java.awt.Point;
import java.util.Set;


public class BoardDrawer extends Application {
    public Stage stage;
    public Scene mainMenuScene;
    public Scene boardScene;
    public Scene aboutScene;
    public static boolean DEBUG = true;
    public static int boardSize = 8;
    public static int cellSize = 100;
    public static int windowLength = boardSize * cellSize;

    public static Color BOARD_COLOR = Color.rgb(0, 159, 3);
    static Board bored = new Board(8);
    public static int[][] board = bored.boardState;

    // Create the scene for the board
    public Scene getBoardScene() {
        // All shapes must be added to this Group to be drawn
        Group root = new Group();

        // Draw border of board
        Rectangle boardBorder = new Rectangle(windowLength, windowLength);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(0,0,0));
        boardBorder.setStrokeWidth(cellSize / 10.0);
        root.getChildren().add(boardBorder);

        double diskRadius = cellSize * 0.8 / 2.0;

        // Draw actual board
        initBoard(root, diskRadius);

        // Get possible moves for this turn
        Set<Point> nextMoves = new HashSet<>();
        nextMoves.addAll(bored.getValidMoves());

        // Colors for the possible move ring whether moused over or not
        Color RING_GRAY = Color.rgb(0,0,0,0.2);
        Color RING_GRAY_HOVERED = Color.rgb(0,0,0,0.5);

        // Draw rings to indicate possible moves
        for (Point p : nextMoves) {
            // Dark circle base
            Circle darkLayer = new Circle(p.getX() * cellSize + cellSize/2.0, p.getY() * cellSize + cellSize/2.0, cellSize*0.8/2);
            darkLayer.setFill(RING_GRAY);

            // Circle that forms the "hole"
            Circle lightLayer = new Circle(p.getX() * cellSize + cellSize/2.0, p.getY() * cellSize + cellSize/2.0, cellSize*0.6/2);
            lightLayer.setFill(BOARD_COLOR);

            // Darken circle when moused over
            darkLayer.setOnMouseEntered(event -> {
                darkLayer.setFill(RING_GRAY_HOVERED);
                event.consume();
            });
            // Darkens ring even when mouse is inside ring
            lightLayer.setOnMouseEntered(event -> {
                darkLayer.setFill(RING_GRAY_HOVERED);
                event.consume();
            });
            // Lighten when mouse exits ring
            darkLayer.setOnMouseExited(event -> {
                darkLayer.setFill(RING_GRAY);
            });

            root.getChildren().add(darkLayer);
            root.getChildren().add(lightLayer);
        }


        // mouse click test
        // TODO: Send cell coords back to pack.Board.java when clicked
        root.setOnMouseClicked(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            if (DEBUG) System.out.println("Mouse clicked at: (" + mouseX + ", " + mouseY + ")");

            // Convert raw pixel coords to cell row and col
            int colClicked = (int)(mouseX / cellSize);
            int rowClicked = (int)(mouseY / cellSize);
            Point clicked = new Point(rowClicked, colClicked);
            if (DEBUG) System.out.println("Cell clicked: " + rowClicked + ", " + colClicked);
            if (DEBUG && nextMoves.contains(clicked)){
                System.out.println("valid spot!");
                bored.makeMove(clicked);

                root.getChildren().clear();
                root.getChildren().add(boardBorder);

                initBoard(root, diskRadius);

                nextMoves.clear();
                nextMoves.addAll(bored.getValidMoves());

                for (Point p : nextMoves) {
                    // Dark circle base

                    Circle darkLayer = new Circle(p.getY() * cellSize + cellSize/2.0, p.getX() * cellSize + cellSize/2.0, cellSize*0.8/2);
                    darkLayer.setFill(RING_GRAY);

                    // Circle that forms the "hole"
                    Circle lightLayer = new Circle(p.getY() * cellSize + cellSize/2.0, p.getX() * cellSize + cellSize/2.0, cellSize*0.6/2);
                    lightLayer.setFill(BOARD_COLOR);

                    // Darken circle when moused over
                    darkLayer.setOnMouseEntered(event2 -> {
                        darkLayer.setFill(RING_GRAY_HOVERED);
                        event2.consume();
                    });
                    // Darkens ring even when mouse is inside ring
                    lightLayer.setOnMouseEntered(event2 -> {
                        darkLayer.setFill(RING_GRAY_HOVERED);
                        event2.consume();
                    });
                    // Lighten when mouse exits ring
                    darkLayer.setOnMouseExited(event2 -> {
                        darkLayer.setFill(RING_GRAY);
                    });
                    root.getChildren().add(darkLayer);
                    root.getChildren().add(lightLayer);
                }
            }
        });

        // Draw window containing the group with all our cool graphics
        Scene scene = new Scene(root, 8 * cellSize, 8 * cellSize);
        scene.setFill(Color.rgb(220,220,220));

        return scene;
    }

    private void initBoard(Group root, double diskRadius) {
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                double xPos = c * cellSize;
                double yPos = r * cellSize;

                //redrawing green cells
                Rectangle cell = new Rectangle(xPos, yPos, cellSize, cellSize);
                cell.setFill(BOARD_COLOR);
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(cellSize / 20.0);
                root.getChildren().add(cell);

                // Draw disks; Must add half of cell length since circles draw from center
                double xPosDisk = xPos + cellSize / 2.0;
                double yPosDisk = yPos + cellSize / 2.0;
                Color diskColor = Color.TRANSPARENT;

                switch(board[r][c]) {
                    case 1:
                        diskColor = Color.WHITE;
                        if (DEBUG) System.out.println("Placed white disk at " + xPosDisk + "," + yPosDisk);
                        break;
                    case 2:
                        diskColor = Color.BLACK;
                        if (DEBUG) System.out.println("Placed black disk at " + xPosDisk + "," + yPosDisk);
                        break;
                }

                Circle disk = new Circle(xPosDisk, yPosDisk, diskRadius);
                disk.setFill(diskColor);
                root.getChildren().add(disk);
            }
        }
    }

    public void start(Stage stage) {
        // Get all the scenes
        this.stage = stage;
        this.boardScene = getBoardScene();
        this.mainMenuScene = MainMenu.getMainMenuScene(this.stage, this.boardScene, this.aboutScene);
        this.aboutScene = MainMenu.getAboutScene(this.stage, this.mainMenuScene);

        // This is kinda cursed. When the Main Menu scene is created, it has a button to the About scene,
        // but it hasn't been created yet! So the button freezes the program.
        // I just recreate the scene again, now that the About scene exists.
        this.mainMenuScene = MainMenu.getMainMenuScene(this.stage, this.boardScene, this.aboutScene);

        // Pick new random window title on each startup
        String[] titles = {"Parathello: A pursuit in plundering every potential piece by probing possible paths in parallel",
                           "Parathello: Parallel Othello",
                           "Othello, but it's optimized in parallel",
                           "this is nOT HELLO world, but something much more",
                           "Finally, something that's not image processing or matrix multiplication!",
                           "Another board game, but not Sudoku or Chess"};
        String windowTitle = titles[(int)(Math.random() * titles.length)];

        // Initially show the main menu
        stage.setScene(mainMenuScene);
        stage.setTitle(windowTitle);
        stage.show();
    }
}
