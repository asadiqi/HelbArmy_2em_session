package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View {
    private Controller controller;
    private Label label;

    public View(Controller controller) {
        this.controller = controller;
    }

    public void start(Stage stage) {
        label = new Label("Hello!");

        Button button = new Button("Click Me");
        button.setOnAction(e -> controller.handleHelloButtonClick());

        VBox layout = new VBox(10, label, button);
        Scene scene = new Scene(layout, 320, 240);

        stage.setTitle("JavaFX MVC");
        stage.setScene(scene);
        stage.show();
    }

    public void updateLabel(String message) {
        label.setText(message);
    }
}