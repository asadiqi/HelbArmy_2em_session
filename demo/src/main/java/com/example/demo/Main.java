package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Controller controller = new Controller();
        View view = new View(controller);
        controller.setView(view);

        view.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}