package com.example.demo;

import java.util.List;
import java.util.Random;

public class GameElement {
    protected int x;
    protected int y;
    protected double defaultRatio = 1.0;
    public static final GameElement NO_POSITION = new GameElement(-1, -1); // Position invalide (aucune position)



    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getImagePath() {
        return null; // Classe mère → pas de chemin d'image par défaut
    }

    public double getWidthRatio() {
        return defaultRatio;
    }

    public double getHeightRatio() {
        return defaultRatio;
    }

    public double getDistanceWith(GameElement other) {
        int dx = other.getX() - this.getX();
        int dy = other.getY() - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Renvoie true si la case (x,y) est occupée par un GameElement.
     */
    public static boolean isOccupied(int x, int y, List<GameElement> elements) {
        for (GameElement element : elements) {
            if (element.getX() == x && element.getY() == y) {
                return true; // occupée
            }
        }
        return false; // libre
    }

    /**
     * Renvoie une case libre aléatoire dans la grille.
     */
    public static GameElement getRandomFreeCell(int gridCols, int gridRows, List<GameElement> allElements) {
        Random rand = new Random();
        int maxAttempts = 100;

        for (int i = 0; i < maxAttempts; i++) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);
            if (!isOccupied(x, y, allElements)) { // on veut une case libre
                return new GameElement(x, y);
            }
        }
        return NO_POSITION; // pas trouvé
    }
}
