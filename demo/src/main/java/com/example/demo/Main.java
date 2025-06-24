package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Controller controller = new Controller();
        View view = new View(controller);
        controller.setView(view);
        stage.setScene(view.scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}