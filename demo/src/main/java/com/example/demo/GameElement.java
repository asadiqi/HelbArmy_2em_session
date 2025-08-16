package com.example.demo;

import com.example.demo.collectable.Flag;
import com.example.demo.collectable.MagicStone;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameElement {

    private static final int INIT_INDEX = 0;
    protected static final double DEFAULT_RATIO = 1.0;
    protected static final int INVALID_POSITION = -1;
    protected static final int MAX_RANDOM_ATTEMPTS = 100;
    protected int x;
    protected int y;

    public static final GameElement NO_POSITION = new GameElement(INVALID_POSITION , INVALID_POSITION ); // Position invalide (aucune position)

    // Constructeur de la classe GameElement
    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Retourne la coordonnée X de l'élément.
    public int getX() {
        return x;
    }

    // Retourne la coordonnée Y de l'élément.
    public int getY() {
        return y;
    }

    // Retourne le chemin de l'image de l'élément.
    // Ici renvoie null car c'est la classe de base.
    public String getImagePath() {
        return null; // Classe mère → pas de chemin d'image par défaut
    }

    // Retourne le ratio de largeur de l'élément pour l'affichage.
    // Par défaut 1.0.
    public double getWidthRatio() {
        return DEFAULT_RATIO;
    }

    // Retourne le ratio de hauteur de l'élément pour l'affichage.
    // Par défaut 1.0.
    public double getHeightRatio() {
        return DEFAULT_RATIO;
    }

    // Calcule la distance euclidienne entre cet élément et un autre.
    // other : l'autre élément.
    // Retourne la distance.
    public double getDistanceWith(GameElement other) {
        int dx = other.getX() - this.getX();
        int dy = other.getY() - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Vérifie si la case (x, y) est déjà occupée par un élément autre qu'un Flag ou une MagicStone.
    // elements : liste de tous les éléments sur le plateau.
    // Retourne true si occupée, false sinon.
    public static boolean isOccupied(int x, int y, List<GameElement> elements) {
        for (GameElement element : elements) {
            if (element.getX() == x && element.getY() == y) {
                if (element instanceof  MagicStone || element instanceof  Flag) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

    // Cherche une case libre aléatoire sur le plateau.
    // gridCols/gridRows : dimensions du plateau, allElements : tous les éléments.
    // Retourne une nouvelle GameElement à la position libre ou NO_POSITION si aucune trouvée après MAX_RANDOM_ATTEMPTS essais.
    public static GameElement getRandomFreeCell(int gridCols, int gridRows, List<GameElement> allElements) {
        Random rand = new Random();
        int maxAttempts = MAX_RANDOM_ATTEMPTS; // Nombre maximum d'essais pour trouver une case libre
        // Boucle pour essayer de trouver une case libre
        for (int i = INIT_INDEX; i < maxAttempts; i++) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            // Vérifie si la position est libre (pas occupée par un élément autre qu'un Flag ou une MagicStone)
            if (!isOccupied(x, y, allElements)) {
                return new GameElement(x, y);
            }
        }

        // Si aucune case libre n'est trouvée après tous les essais
        System.out.println("No free position found after " + maxAttempts + " attempts.");
        return NO_POSITION; // Retourne une position null
    }
}