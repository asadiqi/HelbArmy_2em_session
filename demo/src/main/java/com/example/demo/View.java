package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class View {
    Label label = new Label("Texte ici");
    Button button = new Button("Clique moi");
    VBox layout = new VBox(10, label, button);
    Scene scene = new Scene(layout, 300, 200);

    public View(Controller controller) {
        button.setOnAction(e -> controller.onButtonClick());
    }

    public void updateLabel(String text) {
        label.setText(text);
    }
}
