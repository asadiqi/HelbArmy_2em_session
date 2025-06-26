package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class View {
    private static final double INITIAL_WIDTH = 700;
    private static final double INITIAL_HEIGHT = 700;
    private static final String LIGHT_GREEN_COLOR = "AAD751";
    private static final String DARK_GREEN_COLOR = "8CBF3F";
    private static final String WINDOW_TITLE = "Grille MVC Canvas";
    private static final int CHECKER_DIVISOR = 2;
    private static final int INITIAT_INDEX = 0;

    private Canvas canvas;
    private Scene scene;
    private Pane pane;
    private Controller controller;

    public View(Controller controller, Stage stage) {
        this.controller = controller;

        pane = new Pane();
        canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        pane.getChildren().add(canvas);

        scene = new Scene(pane, INITIAL_WIDTH, INITIAL_HEIGHT);

        drawGrid();

        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private void drawGrid() {
        int rows = controller.getGridRows();
        int cols = controller.getGridCols();

        double cellWidth = canvas.getWidth() / cols;
        double cellHeight = canvas.getHeight() / rows;

        GraphicsContext gc = canvas.getGraphicsContext2D();

        for (int row = INITIAT_INDEX; row < rows; row++) {
            for (int col = INITIAT_INDEX; col < cols; col++) {
                if ((row + col) % CHECKER_DIVISOR == INITIAT_INDEX) {
                    gc.setFill(Color.web(LIGHT_GREEN_COLOR));
                } else {
                    gc.setFill(Color.web(DARK_GREEN_COLOR));
                }
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}
