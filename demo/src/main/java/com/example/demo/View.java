package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class View {
    private Scene scene;
    private Pane pane;
    private Label[][] labels = new Label[20][20];

    public View(Controller controller, Stage stage) {
        pane = new Pane();
        scene = new Scene(pane, 650, 650);

        createGrid();

        // Quand la taille change, on redimensionne la grille
        scene.widthProperty().addListener((obs, oldVal, newVal) -> resizeLabels());
        scene.heightProperty().addListener((obs, oldVal, newVal) -> resizeLabels());

        resizeLabels();

        stage.setTitle("Grille 20x20 responsive simple");
        stage.setScene(scene);
        stage.show();
    }

    private void createGrid() {
        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                Label label = new Label();
                label.setStyle("-fx-border-color: black; -fx-background-color: white;");
                labels[row][col] = label;
                pane.getChildren().add(label);
            }
        }
    }

    private void resizeLabels() {
        double width = scene.getWidth();
        double height = scene.getHeight();

        double cellWidth = width / 20;
        double cellHeight = height / 20;

        for (int row = 0; row < 20; row++) {
            for (int col = 0; col < 20; col++) {
                Label label = labels[row][col];
                label.setLayoutX(col * cellWidth);
                label.setLayoutY(row * cellHeight);
                label.setPrefWidth(cellWidth);
                label.setPrefHeight(cellHeight);
            }
        }
    }

    public Scene getScene() {
        return scene;
    }
}
