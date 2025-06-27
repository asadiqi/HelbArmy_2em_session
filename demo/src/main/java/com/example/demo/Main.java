package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {

        View view = new View(stage);
        Controller controller = new Controller(view);

    }

    public static void main(String[] args) {
        launch();
    }
}
