// NOTE: This may not compile yet, as certain JavaFX modules must be set in the project first

// no wildcard imports :[
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class BoardDrawer extends Application {
    public static int boardSize = 8;
    public static int cellSize = 100;
    public static int[][] board =
            {
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 1, 2, 0, 0, 0},
                    {0, 0, 0, 2, 1, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0},
                    {0, 0, 0, 0, 0, 0, 0, 0}
            };

    public void start(Stage stage) {
        Group root = new Group();

        Rectangle boardBorder = new Rectangle(boardSize * cellSize, boardSize * cellSize);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(0,0,0));
        boardBorder.setStrokeWidth(cellSize / 10.0);
        root.getChildren().add(boardBorder);

        // Draw actual board
        for (int r = 0; r < boardSize; r++) {
            for (int c = 0; c < boardSize; c++) {
                // Draw green square
                Rectangle cell = new Rectangle(r*cellSize, c*cellSize, cellSize, cellSize);
                cell.setFill(Color.rgb(0, 159, 3));
                cell.setStroke(Color.BLACK);
                cell.setStrokeWidth(cellSize / 20.0);
                root.getChildren().add(cell);

                // Draw disks
                double diskRadius = cellSize * 0.8 / 2.0;
                Circle disk = new Circle(r*cellSize+cellSize/2.0, c*cellSize+cellSize/2.0, diskRadius);
                switch (board[r][c]) {
                    case 0:
                        disk.setFill(Color.TRANSPARENT);
                        break;
                    case 1:
                        disk.setFill(Color.WHITE);
                        break;
                    case 2:
                        disk.setFill(Color.BLACK);
                        break;
                }
                root.getChildren().add(disk);
            }
        }


        Scene scene = new Scene(root, 800, 800);
        scene.setFill(Color.rgb(220,220,220));


        stage.setTitle("othello board yay");
        stage.setScene(scene);
        stage.show();
    }
}
