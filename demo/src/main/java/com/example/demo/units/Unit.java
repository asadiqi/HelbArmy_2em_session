package com.example.demo.units;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Unit extends GameElement {
    protected GameElement target = new GameElement(-1, -1);



    public Unit(GameElement position) {
        super(position.getX(),position.getY());
    }


    public GameElement getTarget() {
        return target;
    }

    public boolean hasValidTarget() {
        return target.getX() >= 0 && target.getY() >= 0;
    }

    public boolean hasReachedTarget() {
        return hasValidTarget() && x == target.getX() && y == target.getY();
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public boolean isCoordinateInBoard(GameElement coord, int maxX, int maxY) {
        return coord.getX() >= 0 && coord.getY() >= 0 && coord.getX() < maxX && coord.getY() < maxY;
    }

    public List<GameElement> getAccessibleAdjacentCoordinates(GameElement coord, int maxX, int maxY, List<GameElement> occupied) {
        List<GameElement> result = new ArrayList<>();
        int[] dx = {0, -1, 1, 0};
        int[] dy = {-1, 0, 0, 1};

        for (int i = 0; i < 4; i++) {
            int newX = coord.getX() + dx[i];
            int newY = coord.getY() + dy[i];
            GameElement adjacent = new GameElement(newX, newY);

            if (isCoordinateInBoard(adjacent, maxX, maxY) && !isOccupied(adjacent, occupied)) {
                result.add(adjacent);
            }
        }
        return result;
    }

    private boolean isOccupied(GameElement coord, List<GameElement> occupied) {
        for (GameElement e : occupied) {
            if (e.getX() == coord.getX() && e.getY() == coord.getY()) {
                return true;
            }
        }
        return false;
    }

    public GameElement getNextCoordinateForTarget(GameElement current, GameElement target, int maxX, int maxY, List<GameElement> occupied) {
        double minDistance = maxX + maxY;
        GameElement best = new GameElement(current.getX(), current.getY()); // pas de mouvement par défaut

        for (GameElement adj : getAccessibleAdjacentCoordinates(current, maxX, maxY, occupied)) {
            double distance = adj.getDistanceWith(target);
            if (distance < minDistance) {
                minDistance = distance;
                best = adj;
            }
        }
        return best;
    }

    // méthode pour avancer vers la cible (controller appelle ça)
    public void moveTowardsTarget(int maxX, int maxY, List<GameElement> occupied) {
        if (!hasValidTarget()) return;

        GameElement current = new GameElement(this.x, this.y);
        GameElement next = getNextCoordinateForTarget(current, target, maxX, maxY, occupied);
        setPosition(next.getX(), next.getY());
    }

    public void setTarget(GameElement target) {
        if (target != null) {
            this.target = target;
        }
    }

}

