package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Assassin extends Unit {

    public static String northAssassinPath = "img/white/northAssassin.png";
    public static String southAssassinPath = "img/black/southAssassin.png";
    public static final Assassin NO_ASSASSIN = new Assassin(GameElement.NO_POSITION, null);

    public City city;
    private boolean waiting = false;

    public City getCity() {
        return city;
    }

    public Assassin(GameElement position, City city) {
        super(position);
        this.city = city;
    }

    @Override
    public String getImagePath() {
        return city.isNorth ? northAssassinPath : southAssassinPath;
    }


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
        if (targetEnemy == GameElement.NO_POSITION) {
            targetEnemy = findClosestInList(enemyCollectors);
        }
        if (targetEnemy == GameElement.NO_POSITION) {
            targetEnemy = findClosestInList(enemySeeders);
        }

        // 3. Déplacement ou attente
        if (targetEnemy != GameElement.NO_POSITION) {
            waiting = false;
            setTarget(targetEnemy);
        } else {
            if (waiting) {
                if (!hasReachedTarget()) {
                    moveTowardsTarget(gridCols, gridRows, allElements);
                }
                return;
            } else {
                GameElement randomFreeCell = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);
                if (randomFreeCell != GameElement.NO_POSITION) {
                    setTarget(randomFreeCell);
                    waiting = true;
                }
            }
        }

        moveTowardsTarget(gridCols, gridRows, allElements);
    }


    private GameElement findClosestInList(List<GameElement> enemies) {
        if (enemies.isEmpty()) {
            return GameElement.NO_POSITION;
        }
        GameElement closest = GameElement.NO_POSITION;
        double minDistance = Double.MAX_VALUE;

        for (GameElement enemy : enemies) {
            double distance = this.getDistanceWith(enemy);
            if (distance < minDistance) {
                minDistance = distance;
                closest = enemy;
            }
        }
        return closest;
    }


}
