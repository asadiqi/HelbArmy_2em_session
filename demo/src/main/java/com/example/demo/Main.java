package com.example.demo;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    // Méthode appelée au démarrage de l'application JavaFX
    // stage : la fenêtre principale de l'application
    @Override
    public void start(Stage stage) {


        View view = new View(stage);  // Création de la vue avec la fenêtre principale
        Controller controller = new Controller(view); // Création du contrôleur qui gère la logique du jeu


    }

    // Point d'entrée du programme
    // Lance l'application JavaFX
    public static void main(String[] args) {
        launch();
    }
}
