// NOTE: If you are reading this in the repo, this may not compile yet, as certain JavaFX modules must be set in the project first
package com.example.javafxtest;

// no wildcard imports :[
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashSet;
import java.awt.Point;


public class BoardDrawer extends Application {
    public static boolean DEBUG = true;
    public static int boardSize = 8;
    public static int cellSize = 100;
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

    public void start(Stage stage) {
        // All shapes must be added to this Group to be drawn
        Group root = new Group();

        // Draw border of board
        Rectangle boardBorder = new Rectangle(boardSize * cellSize, boardSize * cellSize);
        boardBorder.setFill(Color.rgb(0,0,0,0));
        boardBorder.setStroke(Color.rgb(0,0,0));
        boardBorder.setStrokeWidth(cellSize / 10.0);
        root.getChildren().add(boardBorder);

        double diskRadius = cellSize * 0.8 / 2.0;
        Color BOARD_COLOR = Color.rgb(0, 159, 3);

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


        stage.setTitle("othello board yay");
        stage.setScene(scene);
        stage.show();
    }
}
