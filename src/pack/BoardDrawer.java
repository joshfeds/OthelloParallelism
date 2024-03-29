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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.awt.Point;


public class BoardDrawer extends Application {
    public Stage stage;
    public Scene mainMenuScene;
    public Scene boardScene;
    public Scene aboutScene;
    public static boolean DEBUG = true;
    public static int boardSize = 8;
    // Note: this variable controls the size of almost everything
    public static int cellSize = 100;
    public static double windowWidth = 1.5 * boardSize * cellSize;
    public static double windowHeight = 1.01 * boardSize * cellSize;
    public static double diskRadius = cellSize * 0.8 / 2.0;
    public static Color BOARD_COLOR = Color.rgb(0, 159, 3);
    public static Color FRAME_COLOR = Color.rgb(72, 35,35);
    static Board bored = new Board(8);
    public static int[][] board = bored.boardState;

    // Create the scene for the board
    public Scene getBoardScene() throws IOException {
        // All shapes must be added to this Group to be drawn
        Group root = new Group();
        MiniJosh gameTree = new MiniJosh(8);

        // Draw border of board
        Rectangle boardBorder = new Rectangle(8 * cellSize, 8 * cellSize);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(0,0,0));
        boardBorder.setStrokeWidth(cellSize / 10.0);
        root.getChildren().add(boardBorder);

        // Draw actual board
        initBoard(root, diskRadius);

        // Get possible moves for this turn
        Set<Point> nextMoves = new HashSet<>();
        for (Point temp : bored.getValidMoves()) {
            nextMoves.add(new Point(temp.x, temp.y));
        }

        // Draw rings to indicate where player can click to place next disk
        drawNextMoveRings(root, nextMoves);

        // Make scene responsive to clicks
        getPlayerInput(root, gameTree, nextMoves);


        // Now, to actually put stuff around the board!
        // This BorderPane places the board in the center, with panels to the left and right.
        BorderPane borderPane = new BorderPane();
        Background boardBackground = new Background(new BackgroundFill(FRAME_COLOR, null, null));
        borderPane.setBackground(boardBackground);

        // Place Othello board in the center, with 0 margins to ensure no excess space is taken.
        borderPane.setCenter(root);
        BorderPane.setMargin(root, new Insets(0));

        // Get scores and names for both players.
        int humanPieces = 2;
        int botPieces = 2;

        String humanName = "You";
        String botName = "Mr. Othello";

        // Left panel with human player's score.
        VBox leftPanel = new VBox();
        leftPanel.setPadding(new Insets(cellSize / 8.0));
        leftPanel.setAlignment(Pos.TOP_CENTER);

        // Constrain panel dimensions.
        leftPanel.setMinHeight(windowHeight);
        leftPanel.setMinWidth(windowWidth / 6.0);
        leftPanel.setMaxHeight(windowHeight);
        leftPanel.setMaxWidth(windowWidth / 6.0);

        Text humanNameText = new Text(humanName);
        humanNameText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        humanNameText.setFill(Color.WHITE);

        Text humanPiecesText = new Text(Integer.toString(humanPieces));
        humanPiecesText.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        humanPiecesText.setFill(Color.WHITE);

        leftPanel.getChildren().addAll(humanNameText, humanPiecesText);

        // TODO: Display player's image.
        //ImageView playerImage = getImage("imgs/playerdefault.PNG");
        //leftPanel.getChildren().addAll(playerImage);

        // Button that goes back to main menu.
        Button backButton = new Button("Back");
        MainMenu.styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(mainMenuScene));

        leftPanel.getChildren().add(backButton);


        // Right panel with bot's score.
        VBox rightPanel = new VBox();
        rightPanel.setPadding(new Insets(cellSize / 8.0));
        rightPanel.setAlignment(Pos.TOP_CENTER);
        rightPanel.setMinHeight(windowHeight);
        rightPanel.setMinWidth(windowWidth / 6.0);
        rightPanel.setMaxHeight(windowHeight);
        rightPanel.setMaxWidth(windowWidth / 6.0);

        Text botNameText = new Text(botName);
        botNameText.setFont(Font.font("Verdana", FontWeight.BOLD, 24));
        botNameText.setFill(Color.WHITE);

        Text botPiecesText = new Text(Integer.toString(botPieces));
        botPiecesText.setFont(Font.font("Verdana", FontWeight.BOLD, 48));
        botPiecesText.setFill(Color.WHITE);

        rightPanel.getChildren().addAll(botNameText, botPiecesText);

        // Add left and right panels.
        borderPane.setLeft(leftPanel);
        borderPane.setRight(rightPanel);





        // Draw window containing the group with all our cool graphics
        Scene scene = new Scene(borderPane, windowWidth, windowHeight);
        scene.setFill(Color.rgb(72, 35,35));

        return scene;
    }

    private ImageView getImage(String filepath) throws IOException {
        InputStream playerImageStream = new FileInputStream(filepath);
        ImageView playerImageView = new ImageView();
        Image playerImage = new Image(playerImageStream);
        playerImageView.setImage(playerImage);

        playerImageView.setFitWidth(1.5 * cellSize);
        playerImageView.setFitHeight(1.5 * cellSize);
        playerImageView.setPreserveRatio(true);

        return playerImageView;
    }
    public void getPlayerInput(Group root, MiniJosh gameTree, Set<Point> nextMoves) {
        // mouse click test
        // TODO: Send cell coords back to pack.Board.java when clicked
        root.setOnMouseClicked(event -> {
            double mouseX = event.getSceneX();
            double mouseY = event.getSceneY();
            if (DEBUG) System.out.println("Mouse clicked at: (" + mouseX + ", " + mouseY + ")");

            // Determine offsets of board's top left corner.
            double xOffset = windowWidth / 6.0 + cellSize / 10.0;
            double yOffset = cellSize / 10.0;
            // Convert raw pixel coords to cell row and col, accounting for offsets
            int colClicked = (int)((mouseX - xOffset) / cellSize);
            int rowClicked = (int)((mouseY - yOffset) / cellSize);
            Point clicked = new Point(rowClicked, colClicked);
            int randVal = 0;
            if (DEBUG) System.out.println("Cell clicked: " + rowClicked + ", " + colClicked);

            if (nextMoves.contains(clicked)){
                bored.makeMove(clicked);
                gameTree.board = bored;

                for(int i = 0; i < gameTree.roots.size(); i++){
                    //System.out.println("Clicked: " + clicked);
                    //System.out.println("Point in root: " + gameTree.roots.get(i).getMove());
                    if(clicked.equals(gameTree.roots.get(i).getMove())){
                        if (DEBUG) System.out.println("We have found our move within the roots.\n");

                        //gameTree.createLeaves(gameTree.roots.get(i));
                        //bored.printBoard();
                        //flag = !flag;
                        if (DEBUG) System.out.println("These are the previous roots: " + gameTree.roots);
                        //System.out.println("gametree board");
                        //gameTree.board.printBoard();

                        gameTree.roots = gameTree.createNodes(true, bored.getBoardState());

                        gameTree.board.printBoard();
                        if (DEBUG) System.out.println("These are the new roots: " + gameTree.roots);
                        int max = gameTree.roots.size();
                        int min = 0;
                        Random random = new Random();
                        randVal = random.nextInt(max - min) + min;

                        if (DEBUG) System.out.println("Rand point: " + gameTree.roots.get(randVal).getMove());
                        break;
                    }

                }

                // Border code from earlier
                Rectangle boardBorder = new Rectangle(8 * cellSize, 8 * cellSize);
                boardBorder.setFill(Color.rgb(0,0,0,0));
                boardBorder.setStroke(Color.rgb(0,0,0));
                boardBorder.setStrokeWidth(cellSize / 10.0);


                root.getChildren().clear();
                root.getChildren().add(boardBorder);

                initBoard(root, diskRadius);

                nextMoves.clear();
                for (Point temp : bored.getValidMoves()) {

                    nextMoves.add(new Point(temp.x, temp.y));
                }

                drawNextMoveRings(root, nextMoves);

                clicked = gameTree.roots.get(randVal).getMove();


                if (DEBUG && nextMoves.contains(clicked)) {
                    bored.makeMove(clicked);
                    gameTree.board = bored;

                    for(int i = 0; i < gameTree.roots.size(); i++){

                        if(clicked.equals(gameTree.roots.get(i).getMove())){
                            if (DEBUG) System.out.println("We have found our move within the roots.\n");

                            if (DEBUG) System.out.println("These are the previous roots: " + gameTree.roots);

                            gameTree.roots = gameTree.createNodes(true, bored.getBoardState());

                            gameTree.board.printBoard();
                            if (DEBUG) System.out.println("These are the new roots: " + gameTree.roots);
                            int max = gameTree.roots.size();
                            int min = 0;
                            Random random = new Random();
                            randVal = random.nextInt(max - min) + min;

                            if (DEBUG) System.out.println("Rand point: " + gameTree.roots.get(randVal).getMove());
                            break;
                        }

                    }


                    root.getChildren().clear();
                    root.getChildren().add(boardBorder);

                    initBoard(root, diskRadius);

                    nextMoves.clear();
                    if (DEBUG) System.out.println("Valid moves from bored.getValidMoves: " + bored.getValidMoves());

                    for(Iterator<Point> it = bored.getValidMoves().iterator(); it.hasNext();){

                        Point temp = it.next();
                        nextMoves.add(new Point(temp.x, temp.y));
                    }

                    drawNextMoveRings(root, nextMoves);
                }

            }
        });
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

    // Draws the rings to indicate next possible moves.
    private void drawNextMoveRings(Group root, Set<Point> nextMoves) {
        // Colors for the possible move ring whether moused over or not
        Color RING_GRAY = Color.rgb(0,0,0,0.2);
        Color RING_GRAY_HOVERED = Color.rgb(0,0,0,0.5);

        // Draw rings to indicate possible moves
        for (Point p : nextMoves) {
            // Dark circle base
            Circle darkLayer = new Circle(p.getY() * cellSize + cellSize/2.0, p.getX() * cellSize + cellSize/2.0, cellSize*0.8/2);
            darkLayer.setFill(RING_GRAY);

            // Circle that forms the "hole"
            Circle lightLayer = new Circle(p.getY() * cellSize + cellSize/2.0, p.getX() * cellSize + cellSize/2.0, cellSize*0.6/2);
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
    }

    // Returns an array containing the number of pieces each player has (not the minimax scores).
    private int[] getPlayerScores() {
        int[] scores = {0,0};
        for (int[] arr : board) {
            for (int piece : arr) {
                if (piece == bored.HUMANPLAYER)
                    scores[0]++;
                else if (piece == bored.BOTPLAYER)
                    scores[1]++;
            }
        }
        return scores;
    }
    public void start(Stage stage) throws IOException{
        // Get all the scenes
        this.stage = stage;
        this.boardScene = getBoardScene();
        this.mainMenuScene = MainMenu.getMainMenuScene(this.stage, this.boardScene, this.aboutScene);
        this.aboutScene = MainMenu.getAboutScene(this.stage, this.mainMenuScene);

        // This is kinda cursed. When some scenes are created, they have buttons to other scenes,
        // but they haven't been created yet! So the button freezes the program.
        // I just recreate the scene again, now that the button's target scene exists.
        this.mainMenuScene = MainMenu.getMainMenuScene(this.stage, this.boardScene, this.aboutScene);

        // The most important scene

        // Pick new random window title on each startup
        String[] titles = {"Parathello: A pursuit in plundering every potential piece by probing possible paths in parallel",
                           "Parathello: Parallel Othello",
                           "Parathello: Othello, but it's optimized in parallel",
                           "Parathello: You may have a big brain, but your opponent has a dozen",
                           "Parathello: You are playing against 8 threads in a trenchcoat",
                           "this is nOT HELLO world, but something much more",
                           "Parathello: Finally, something that's not image processing or matrix multiplication!",
                           "Parathello: Another board game, but not Sudoku or Chess!",
                           "Fun fact: these random titles were inspired by Terraria. oh btw this game is called Parathello"};
        String windowTitle = titles[(int)(Math.random() * titles.length)];

        // Initially show the main menu
        stage.setScene(mainMenuScene);
        stage.setTitle(windowTitle);
        stage.show();
    }
}
