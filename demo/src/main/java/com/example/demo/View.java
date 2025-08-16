package com.example.demo;

import com.example.demo.ressource.Stone;
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

    private static final double INITIAL_WIDTH = 500;
    private static final double INITIAL_HEIGHT = 500;
    private static final String LIGHT_GREEN_COLOR = "AAD751";
    private static final String DARK_GREEN_COLOR = "8CBF3F";
    private static final String WINDOW_TITLE = "HELBArmy";
    private static final int CHECKER_DIVISOR = 2;
    private static final String FILE_PREFIX = "file:";

    private Map<String, Image> imagePath = new HashMap<>(); // Stocke les images déjà chargées pour éviter de les recharger plusieurs fois depuis le disque

    private int rows;
    private int cols;
    private static final int INITIAT_INDEX = 0;

    private Canvas canvas;
    private Pane pane;
    private GraphicsContext gc;

    private double cellWidth;
    private double cellHeight;


    // Constructeur de la vue
    // Paramètre : stage -> la fenêtre principale JavaFX
    // Initialise le panneau et le canvas pour le dessin
    public View(Stage stage) {
        this.stage = stage;
        this.pane = new Pane();
        this.canvas = new Canvas(INITIAL_WIDTH, INITIAL_HEIGHT);
        pane.getChildren().add(canvas);

        // Assure que le jeu se termine correctement quand on ferme la fenêtre
        stage.setOnCloseRequest(event -> {
            controller.endGame();
        });
    }


    // Initialise la vue avec le contrôleur associé
    // controller : le contrôleur qui gère la logique du jeu
    public void initView(Controller controller) {
        this.controller = controller;
        this.scene = new Scene(pane, INITIAL_WIDTH, INITIAL_HEIGHT);

        // Permet de capturer les touches clavier et de les transmettre au contrôleur
        scene.setOnKeyPressed(event -> {
            controller.handleKeyPress(event.getCode());
        });

        // Prépare la grille et dessine tous les éléments
        initiatgrid();
        drawGrid();
        drawAllElements();
        setUpStage();

    }

    // Configure et affiche la fenêtre
    public void setUpStage(){
        stage.setTitle(WINDOW_TITLE);
        stage.setScene(scene);

        stage.show();
    }

    // Dessine la grille sur le canvas
    public void drawGrid() {
        for (int row = INITIAT_INDEX; row < rows; row++) {
            for (int col = INITIAT_INDEX; col < cols; col++) {
                // Alterne les couleurs pour créer un motif "échiquier"
                if ((row + col) % CHECKER_DIVISOR == INITIAT_INDEX) {
                    gc.setFill(Color.web(LIGHT_GREEN_COLOR));
                } else {
                    gc.setFill(Color.web(DARK_GREEN_COLOR));
                }
                gc.fillRect(col * cellWidth, row * cellHeight, cellWidth, cellHeight);
            }
        }
    }

    // Dessine tous les éléments du jeu sur la grille
    public void drawAllElements() {
        // D'abord, on redessine toute la grille (efface l'ancien affichage)
        drawGrid();

        // Ensuite, on dessine les éléments à leurs positions actuelles
        for (GameElement element : controller.getGameElements()) {
            String path = element.getImagePath();
            
            Image img = new Image(FILE_PREFIX + path);
                imagePath.put(path, img);

            // Coordonnées et taille en fonction de la cellule
            double x = element.getX() * cellWidth;
            double y = element.getY() * cellHeight;
            double width = cellWidth*element.getWidthRatio();
            double height = cellWidth*element.getHeightRatio();

            gc.drawImage(img, x, y, width, height);
        }
    }
    
    // Initialise les dimensions de la grille et le contexte graphique
    private void initiatgrid() {
        rows = controller.getGridRows(); // récupère le nombre de lignes
        cols = controller.getGridCols(); // récupère le nombre de colonnes
        cellWidth = canvas.getWidth() / cols; // largeur d'une cellule
        cellHeight = canvas.getHeight() / rows; // hauteur d'une cellule
        gc = canvas.getGraphicsContext2D(); // contexte pour dessiner sur le canvas
    }
}