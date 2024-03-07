// Class to provide all the GUI that is not the main game board.

package pack;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.Stage;

public class MainMenu {
    // Various constants from BoardDrawer
    public static boolean DEBUG = true;
    public static int boardSize = 8;
    public static int cellSize = 100;
    public static double windowWidth = 1.5 * boardSize * cellSize;
    public static double windowHeight = 1.01 * boardSize * cellSize;

    public static Color BOARD_COLOR = Color.rgb(0, 159, 3);
    // Draws the main menu scene containing all the buttons on startup.
    public static Scene getMainMenuScene(Stage stage, Scene boardScene, Scene aboutScene) {
        // BorderPane that holds all the objects below
        BorderPane borderPane = new BorderPane();

        // Green background that looks like the board
        Rectangle boardBackground = new Rectangle(windowWidth, windowHeight);
        boardBackground.setFill(BOARD_COLOR);
        borderPane.getChildren().add(boardBackground);

        // Brown border to frame the board
        Rectangle boardBorder = new Rectangle(windowWidth, windowHeight);
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
        borderPane.setPadding(new Insets((double) windowHeight / 4));

        // Title text
        Text title = new Text("Parathello");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, FontPosture.ITALIC,48));
        title.setStyle("-fx-fill: linear-gradient(to right, white, black); -fx-stroke: black; -fx-stroke-width: 1;");

        // Center text at top of screen
        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        HBox.setMargin(title, new Insets(0, 0, 40, 0));
        borderPane.setTop(titleBox);

        return new Scene(borderPane, windowWidth, windowHeight);
    }

    // Draws the About scene with all our cool names
    public static Scene getAboutScene(Stage stage, Scene mainMenuScene) {
        // BorderPane to hold everything
        BorderPane borderPane = new BorderPane();

        // Green board-looking background like usual
        Rectangle boardBackground = new Rectangle(windowWidth, windowHeight);
        boardBackground.setFill(BOARD_COLOR);
        borderPane.getChildren().add(boardBackground);

        // Brown board border (say that 4 times fast)
        Rectangle boardBorder = new Rectangle(windowWidth, windowHeight);
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
        borderPane.setPadding(new Insets((double) windowHeight / 4));

        // Button to return to main menu
        Button backButton = new Button("Back");
        styleButton(backButton);
        backButton.setOnAction(e -> stage.setScene(mainMenuScene));

        // Need an extra control object to center the button in the bottom center
        HBox bottomBox = new HBox(backButton);
        bottomBox.setAlignment(Pos.CENTER);
        borderPane.setBottom(bottomBox);

        return new Scene(borderPane, windowWidth, windowHeight);
    }

    // Styles the buttons on the main menu and about page.
    public static void styleButton(Button button)
    {
        // Different background styles for a button by default, when mouse hovers over it, and when mouse clicks it.
        Background base = new Background(new BackgroundFill(Color.rgb(76,175,80), new CornerRadii(16), Insets.EMPTY));
        Background hover = new Background(new BackgroundFill(Color.rgb(120,210,130), new CornerRadii(16), Insets.EMPTY));
        Background click = new Background(new BackgroundFill(Color.rgb(120,240,130), new CornerRadii(16), Insets.EMPTY));

        // Style button text.
        button.setBackground(base);
        button.setTextFill(Color.WHITE);
        button.setFont(Font.font("Verdana",32));

        // Events to track mouse activity on this button.
        button.setOnMouseEntered(e -> button.setBackground(hover));
        button.setOnMouseExited(e -> button.setBackground(base));
        button.setOnMousePressed(e -> button.setBackground(click));
        button.setOnMouseReleased(e -> button.setBackground(base));
    }
}
