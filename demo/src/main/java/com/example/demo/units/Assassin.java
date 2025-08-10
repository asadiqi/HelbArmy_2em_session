package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;

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

    public Assassin findClosestEnemyAssassin(List<GameElement> allElements) {
        double minDistance = Double.MAX_VALUE;
        Assassin closest = NO_ASSASSIN;

        for (GameElement element : allElements) {
            if (element instanceof Assassin other &&
                    other != this &&
                    other.city.isNorth != this.city.isNorth) {

                double distance = this.getDistanceWith(other);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = other;
                }
            }
        }
        return closest;
    }

    public void handleAssassin(int gridCols, int gridRows, List<GameElement> allElements) {
        Assassin closestEnemy = findClosestEnemyAssassin(allElements);

        if (closestEnemy != NO_ASSASSIN) {
            waiting = false;
            setTarget(new GameElement(closestEnemy.getX(), closestEnemy.getY()));
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

}
