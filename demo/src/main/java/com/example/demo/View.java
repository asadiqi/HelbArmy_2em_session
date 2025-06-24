package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class View {
    private Label label;
    private Button button;
    private Scene scene;

    public View(Controller controller, Stage stage) {
        label = new Label("Texte ici");
        button = new Button("Clique moi");
        button.setOnAction(e -> controller.onButtonClick());

        VBox layout = new VBox(10, label, button);
        scene = new Scene(layout, 300, 200);

        stage.setTitle("MVC Demo");
    }

    public void updateLabel(String text) {
        label.setText(text);
    }

    public Scene getScene() {
        return scene;
    }
}
