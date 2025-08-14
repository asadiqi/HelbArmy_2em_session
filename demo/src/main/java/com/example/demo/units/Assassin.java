package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Assassin extends Unit {

    private static final int MAX_ADJACENT_DISTANCE = 1; // distance max pour qu’un ennemi soit adjacent
    private static final int COMBAT_OUTCOMES = 2;        // nombre de résultats possibles pour le combat
    private static final int COMBAT_WINNER_ATTACKER = 0; // valeur si l’attaquant gagne
    private boolean waiting = false;  // indique si l'assassin attend avant de se déplacer
    
    public static String northAssassinPath = "img/white/northAssassin.png";
    public static String southAssassinPath = "img/black/southAssassin.png";

    // représente un assassin inexistant.
    // Permet d’éviter d’avoir à écrire null directement dans le code.
    public static final Assassin NO_ASSASSIN = new Assassin(GameElement.NO_POSITION, null);


    // Constructeur de la classe Assassin
    public Assassin(GameElement position, City city) {
        super(position);
        this.city = city;
    }

    // retourne la ville à laquelle appartient l'assassin
    public City getCity() {
        return city;
    }

    // retourne le chemin de l'image selon le camp
    @Override
    public String getImagePath() {
        return city.isNorth ? northAssassinPath : southAssassinPath;
    }

    // gère le comportement de l'assassin
    // gridCols, gridRows : taille de la grille
    // allElements : tous les éléments présents sur la grille
    public void handleAssassin(int gridCols, int gridRows, List<GameElement> allElements) {
        List<GameElement> enemyAssassins = new ArrayList<>();
        List<GameElement> enemyCollectors = new ArrayList<>();
        List<GameElement> enemySeeders = new ArrayList<>();

        // 1. Séparer les ennemis par type et camp
        for (GameElement element : allElements) {
            if (element instanceof Assassin a && a != this && a.city.isNorth != this.city.isNorth) {
                enemyAssassins.add(a);
            } else if (element instanceof Collecter c && c.city.isNorth != this.city.isNorth) {
                enemyCollectors.add(c);
            } else if (element instanceof Seeder s && s.city.isNorth != this.city.isNorth) {
                enemySeeders.add(s);
            }
        }

        // 2. Trouver la cible prioritaire (assassin > collecteur > semeur)
        GameElement targetEnemy = findClosestInList(enemyAssassins);
        if (targetEnemy == GameElement.NO_POSITION) targetEnemy = findClosestInList(enemyCollectors);
        if (targetEnemy == GameElement.NO_POSITION) targetEnemy = findClosestInList(enemySeeders);

        // 3. Vérifier si l'ennemi est adjacent pour le combat
        if (targetEnemy != GameElement.NO_POSITION) {
            int dx = Math.abs(this.x - targetEnemy.getX());
            int dy = Math.abs(this.y - targetEnemy.getY());
            if (dx <= MAX_ADJACENT_DISTANCE  && dy <= MAX_ADJACENT_DISTANCE) {
                // Combat aléatoire
                int winner = (int) (Math.random() * COMBAT_OUTCOMES );
                if (winner == COMBAT_WINNER_ATTACKER) {
                    allElements.remove(targetEnemy);
                    System.err.println(this + " a vaincu " + targetEnemy);

                    // cette unité continue son déplacement
                } else {
                    allElements.remove(this);
                    System.err.println(this + " a été vaincu par " + targetEnemy);
                    return; // cette unité est supprimée, on arrête
                }
            } else {
                setTarget(targetEnemy);
                moveTowardsTarget(gridCols, gridRows, allElements);
            }
        } else {
            // Pas de cible : comportement aléatoire
            if (waiting) {
                if (!hasReachedTarget()) moveTowardsTarget(gridCols, gridRows, allElements);
                return;
            } else {
                GameElement randomFreeCell = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);
                if (randomFreeCell != GameElement.NO_POSITION) {
                    setTarget(randomFreeCell);
                    waiting = true;
                }
            }
            moveTowardsTarget(gridCols, gridRows, allElements);
        }
    }


    // trouve l'ennemi le plus proche dans une liste
    // enemies : liste d'ennemis
    // retourne l'ennemi le plus proche ou NO_POSITION si la liste est vide
    private GameElement findClosestInList(List<GameElement> enemies) {
        // Si la liste est vide, il n'y a pas de cible
        if (enemies.isEmpty()) {
            return GameElement.NO_POSITION;
        }
        GameElement closest = GameElement.NO_POSITION; // initialisation de la cible la plus proche
        double minDistance = Double.MAX_VALUE; // distance minimale initialisée très grande

        for (GameElement enemy : enemies) {
            double distance = this.getDistanceWith(enemy);
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest; // retourne l'ennemi le plus proche trou
    }
}