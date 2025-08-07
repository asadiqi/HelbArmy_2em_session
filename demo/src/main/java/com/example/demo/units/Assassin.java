package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;

import java.util.List;

public class Assassin extends Unit {


    public static String northAssassinPath = "img/white/northAssassin.png";
    public static String southAssassinPath = "img/black/southAssassin.png";

    public City city; // dans Collecter
    private boolean waiting = false;

    public Assassin(GameElement position, City city) {
        super(position);
        this.city = city;
    }

    @Override
    public String getImagePath() {
        return city.isNorth ? northAssassinPath : southAssassinPath;
    }

    public Assassin findClosestEnemyAssassin(java.util.List<GameElement> allElements) {
        double minDistance = Double.MAX_VALUE;
        Assassin closest = null;

        for (GameElement element : allElements) {
            if (element instanceof Assassin other && other != this && other.city.isNorth != this.city.isNorth) {
                double distance = this.getDistanceWith(other);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = other;
                }
            }
        }
        return closest;
    }

    public void update (int gridCols, int gridRows, List<GameElement> allElements) {
        Assassin closesEnemy = findClosestEnemyAssassin(allElements);

        if (closesEnemy != null){
            waiting = false ;
            setTarget(new GameElement(closesEnemy.getX(), closesEnemy.getY()));
        } else {
            if (waiting) {
                if (!hasReachedTarget()) {
                    moveTowardsTarget(gridCols, gridRows, allElements);
                }
                return;
            } else {
                GameElement randomFreeCell = GameElement.getRandomFreeCell(gridCols,gridRows,allElements);
                setTarget(randomFreeCell);
                waiting = true;
            }
        }
        moveTowardsTarget(gridCols, gridRows, allElements);
    }
}


