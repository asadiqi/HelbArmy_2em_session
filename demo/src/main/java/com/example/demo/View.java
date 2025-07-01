package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class View {

    private Stage stage;
    private Scene scene;
    private Controller controller;

    private static final double INITIAL_WIDTH = 700;
    private static final double INITIAL_HEIGHT = 700;
    private static final String LIGHT_GREEN_COLOR = "AAD751";
    private static final String DARK_GREEN_COLOR = "8CBF3F";
    private static final String WINDOW_TITLE = "Grille MVC Canvas";
    private static final int CHECKER_DIVISOR = 2;
    private Map<String, Image> imagePath = new HashMap<>();// savoir expliquer au prof

    private int rows;
    private int cols;
    private static final int INITIAT_INDEX = 0;

    private final int NORTH = 0;
    private final int SOUTH = 1;

    private Canvas canvas;
    private Pane pane;
    private GraphicsContext gc;

    private double cellWidth;
    private double cellHeight;

    public View(Stage stage) {
        this.stage = stage;
        this.pane = new Pane();
        this.canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        pane.getChildren().add(canvas);
    }


    public void initView(Controller controller) {
        this.controller = controller;
        this.scene = new Scene(pane, INITIAL_WIDTH, INITIAL_HEIGHT);

        initiatgrid();
        drawGrid();
        drawAllElements();
        setUpStage();

    }

    public void setUpStage(){
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private void initiatgrid() {
        rows = controller.getGridRows();
        cols = controller.getGridCols();
        cellWidth = canvas.getWidth() / cols;
        cellHeight = canvas.getHeight() / rows;
        gc = canvas.getGraphicsContext2D();
    }

    private void drawGrid() {
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

    public void drawAllElements() {
        for (GameElement element : controller.getGameElements()) {
            String path = element.getImagePath();
            Image img = imagePath.get(path);
                img = new Image("file:" + path);
                imagePath.put(path, img);

            double x = element.getX() * cellWidth;
            double y = element.getY() * cellHeight;
            gc.drawImage(img, x, y, cellWidth, cellHeight);
        }
    }


}
