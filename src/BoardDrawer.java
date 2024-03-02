// NOTE: If you are reading this in the repo, this may not compile yet, as certain JavaFX modules must be set in the project first
// the package statement below is just an artifact of whatever hacky intellij environment I set up just to get this to compile. we still don't have one set up for this project specifically
package org.example.othelloparallelism2real2cool;

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

import java.util.HashSet;
import java.awt.Point;


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
    public static int[][] board =
            {
                    {1, 1, 1, 1, 1, 1, 1, 1},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 2, 0, 0, 0},
                    {0, 0, 0, 2, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0}
            };

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
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                // Swap the r and c to reflect board across main diagonal
                double xPos = c * cellSize;
                double yPos = r * cellSize;

                // Draw green square
                Rectangle cell = new Rectangle(xPos, yPos, cellSize, cellSize);
                cell.setFill(BOARD_COLOR);
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(cellSize / 20.0);
                root.getChildren().add(cell);

                // Draw disks; Must add half of cell length since circles draw from center
                double xPosDisk = xPos + cellSize / 2.0;
                double yPosDisk = yPos + cellSize / 2.0;
                Color diskColor = Color.TRANSPARENT;

                // If the cell has a 1 or 2, draw white or black disk; else, it will stay transparent
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

        // test various possible moves
        // TODO: write function to grab these from Board.java
        HashSet<Point> nextMoves = new HashSet<Point>();
        nextMoves.add(new Point(2,3));
        nextMoves.add(new Point(3,2));
        nextMoves.add(new Point(4,5));
        nextMoves.add(new Point(5,4));

        // Colors for the possible move ring whether moused over or not
        Color RING_GRAY = Color.rgb(0,0,0,0.2);
        Color RING_GRAY_MOUSED = Color.rgb(0,0,0,0.5);

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
                darkLayer.setFill(RING_GRAY_MOUSED);
                event.consume();
            });
            // Darkens ring even when mouse is inside ring
            lightLayer.setOnMouseEntered(event -> {
                darkLayer.setFill(RING_GRAY_MOUSED);
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
        // TODO: Send cell coords back to Board.java when clicked
        root.setOnMouseClicked(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            if (DEBUG) System.out.println("Mouse clicked at: (" + mouseX + ", " + mouseY + ")");

            // Convert raw pixel coords to cell row and col
            int colClicked = (int)(mouseX / cellSize);
            int rowClicked = (int)(mouseY / cellSize);
            if (DEBUG) System.out.println("Cell clicked: " + rowClicked + ", " + colClicked);
            if (DEBUG && nextMoves.contains(new Point(colClicked, rowClicked))) System.out.println("valid spot!");
        });

        // Draw window containing the group with all our cool graphics
        Scene scene = new Scene(root, 8 * cellSize, 8 * cellSize);
        scene.setFill(Color.rgb(220,220,220));
        return scene;
    }

    // Draws the main menu scene containing all the buttons on startup.
    public Scene getMainMenuScene() {
        // BorderPane that holds all the objects below
        BorderPane borderPane = new BorderPane();

        // Green background that looks like the board
        Rectangle boardBackground = new Rectangle(windowLength, windowLength);
        boardBackground.setFill(BOARD_COLOR);
        borderPane.getChildren().add(boardBackground);

        // Brown border to frame the board
        Rectangle boardBorder = new Rectangle(windowLength, windowLength);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(70,30,30));
        boardBorder.setStrokeWidth(cellSize / 5.0);
        borderPane.getChildren().add(boardBorder);

        // This holds all the buttons
        StackPane menuLayout = new StackPane();

        // Button time
        Button playButton = new Button("Play");
        Button aboutButton = new Button("About");
        Button exitButton = new Button("Exit");

        // Apply same style to all buttons
        styleButton((playButton));
        styleButton((aboutButton));
        styleButton((exitButton));

        // Tell buttons what to do
        playButton.setOnAction(e -> stage.setScene(boardScene));
        aboutButton.setOnAction(e -> stage.setScene(aboutScene));
        exitButton.setOnAction(e -> System.exit(0));

        // Draw and align buttons
        menuLayout.getChildren().addAll(playButton, aboutButton, exitButton);
        StackPane.setAlignment(playButton, Pos.TOP_CENTER);
        StackPane.setAlignment(aboutButton, Pos.CENTER);
        StackPane.setAlignment(exitButton, Pos.BOTTOM_CENTER);

        // Center the button array in the scene
        borderPane.setCenter(menuLayout);
        borderPane.setPadding(new Insets((double) windowLength / 4));

        // Title text
        Text title = new Text("Parathello");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,48));
        title.setStyle("-fx-fill: linear-gradient(to right, white, black); -fx-stroke: black; -fx-stroke-width: 1;");

        // Center text at top of screen
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        HBox.setMargin(title, new Insets(0, 0, 40, 0));
        borderPane.setTop(titleBox);

        return new Scene(borderPane, windowLength, windowLength);
    }

    // Styles the buttons on the main menu and about page.
    public static void styleButton(Button button)
    {
        // Different background styles for a button by default, when mouse hovers over it, and when mouse clicks it.
        Background base = new Background(new BackgroundFill(Color.rgb(76,175,80), new CornerRadii(16), Insets.EMPTY));
        Background hover = new Background(new BackgroundFill(Color.rgb(120,210,130), new CornerRadii(16), Insets.EMPTY));
        Background click = new Background(new BackgroundFill(Color.rgb(120,240,130), new CornerRadii(16), Insets.EMPTY));

        button.setBackground(base);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("Verdana",32));

        // Events to track mouse activity on this button.
        button.setOnMouseEntered(e -> button.setBackground(hover));
        button.setOnMouseExited(e -> button.setBackground(base));
        button.setOnMousePressed(e -> button.setBackground(click));
        button.setOnMouseReleased(e -> button.setBackground(base));
    }

    // Draws the About scene with all our cool names
    public Scene getAboutScene() {
        // BorderPane to hold everything
        BorderPane borderPane = new BorderPane();

        // Green board-looking background like usual
        Rectangle boardBackground = new Rectangle(windowLength, windowLength);
        boardBackground.setFill(BOARD_COLOR);
        borderPane.getChildren().add(boardBackground);

        // Brown board border (say that 4 times fast)
        Rectangle boardBorder = new Rectangle(windowLength, windowLength);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(70,30,30));
        boardBorder.setStrokeWidth(cellSize / 5.0);
        borderPane.getChildren().add(boardBorder);

        // oh wow that's us
        Text credits = new Text("Created By:\nJarod Davies\nJoshua Federman\nAnna MacInnis\nNicholas Rolland");
        credits.setFont(Font.font("Verdana", 32));
        credits.setFill(Color.WHITE);
        credits.setTextAlignment(TextAlignment.CENTER);
        credits.setLineSpacing(20);

        // Center credits text in scene
        borderPane.setCenter(credits);
        borderPane.setPadding(new Insets((double) windowLength / 4));

        // Button to return to main menu
        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(mainMenuScene));

        // Need an extra control object to center the button in the bottom center
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(bottomBox);

        return new Scene(borderPane, windowLength, windowLength);
    }

    public void start(Stage stage) {
        // Get all the scenes
        this.stage = stage;
        this.boardScene = getBoardScene();
        this.mainMenuScene = getMainMenuScene();
        this.aboutScene = getAboutScene();


        // Initially show the main menu
        stage.setScene(mainMenuScene);
        stage.setTitle("parathello");
        stage.show();
    }
}
