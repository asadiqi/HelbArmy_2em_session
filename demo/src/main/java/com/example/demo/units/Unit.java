package com.example.demo.units;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Unit extends GameElement {
    protected GameElement target = new GameElement(-1, -1);
    public static final GameElement NO_POSITION = new GameElement(-1, -1);

    public Unit(GameElement position) {
        super(position.getX(), position.getY());
    }

    public GameElement getTarget() {
        return target;
    }

    public boolean hasValidTarget() {
        return target.getX() >= 0 && target.getY() >= 0;
    }

    public boolean hasReachedTarget() {
        if (!hasValidTarget()) return false;
        int dx = Math.abs(x - target.getX());
        int dy = Math.abs(y - target.getY());
        // accept adjacent or same cell
        return dx <= 1 && dy <= 1;
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public boolean isCoordinateInBoard(GameElement coord, int maxX, int maxY) {
        return coord.getX() >= 0 && coord.getY() >= 0
                && coord.getX() < maxX && coord.getY() < maxY;
    }

    public List<GameElement> getAccessibleAdjacentCoordinates(GameElement coord, int maxX, int maxY, List<GameElement> occupied) {
        List<GameElement> result = new ArrayList<>();
        int[] dx = {-1,-1,-1,0,0,1,1,1};
        int[] dy = {-1, 0, 1, -1,-1,-1,0,1};

        for (int i = 0; i < dx.length; i++) {
            int newX = coord.getX() + dx[i];
            int newY = coord.getY() + dy[i];
            GameElement adjacent = new GameElement(newX, newY);

            // case accessible = dans le plateau ET non occupée
            if (isCoordinateInBoard(adjacent, maxX, maxY) &&
                    !GameElement.isOccupied(adjacent.getX(), adjacent.getY(), occupied)) {
                result.add(adjacent);
            }
        }
        return result;
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

        if (hasReachedTarget()) {
            // déjà proche de la cible, on reste sur place
            return;
        }
        GameElement current = new GameElement(this.x, this.y);
        GameElement next = getNextCoordinateForTarget(current, target, maxX, maxY, occupied);
        setPosition(next.getX(), next.getY());
    }

    public void setTarget(GameElement target) {
        if (target != null) {
            this.target = target;
        }
    }

    public static GameElement findNearestFreeCoordinate(GameElement startPos, int maxDistance, int maxX, int maxY, List<GameElement> occupied) {
        for (int dist = 1; dist <= maxDistance; dist++) {
            for (int dx = -dist; dx <= dist; dx++) {
                int dy = dist - Math.abs(dx);

                int x1 = startPos.getX() + dx;
                int y1 = startPos.getY() + dy;

                if (isValidAndFree(x1, y1, maxX, maxY, occupied)) {
                    return new GameElement(x1, y1);
                }

                if (dy != 0) {
                    int x2 = startPos.getX() + dx;
                    int y2 = startPos.getY() - dy;

                    if (isValidAndFree(x2, y2, maxX, maxY, occupied)) {
                        return new GameElement(x2, y2);
                    }
                }
            }
        }
        return NO_POSITION; // position invalide
    }

    private static boolean isValidAndFree(int x, int y, int maxX, int maxY, List<GameElement> occupied) {
        if (x < 0 || x >= maxX || y < 0 || y >= maxY) return false;

        // libre si NON occupé
        return !GameElement.isOccupied(x, y, occupied);
    }
}
