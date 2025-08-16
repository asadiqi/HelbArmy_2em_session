package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;
import com.example.demo.collectable.MagicStone;


import java.util.ArrayList;
import java.util.List;

public abstract class Unit extends GameElement {


    private static final int OFFSET_NEGATIVE = -1;
    private static final int OFFSET_ZERO = 0;
    private static final int OFFSET_POSITIVE = 1;
    private static final int MIN_DISTANCE = 1;
    private static final String NORTH_LABEL = "Nord";
    private static final String SOUTH_LABEL = "Sud";
    private static  final int INIT_INDEX = 0;

    protected GameElement target = GameElement.NO_POSITION; // Cible non définie (position invalide)
    public City city;


    // Constructeur de la classe Unit
    public Unit(GameElement position) {
        super(position.getX(), position.getY());
    }

    // Vérifie si la cible de l'unité est définie et valide.
    // Retourne true si la cible a des coordonnées >= 0, sinon false.
    public boolean hasValidTarget() {
        return target.getX() >= INIT_INDEX && target.getY() >= INIT_INDEX;
    }

    // Vérifie si la cible de l'unité est définie et valide.
    // Retourne true si la cible a des coordonnées >= 0, sinon false.
    public boolean hasReachedTarget() {
        if (!hasValidTarget()) return false;
        int dx = Math.abs(x - target.getX());
        int dy = Math.abs(y - target.getY());
        // accept adjacent ou meme cellule
        return dx <= MIN_DISTANCE && dy <= MIN_DISTANCE;
    }

    // Déplace l'unité aux coordonnées spécifiées.
    // newX et newY : nouvelles coordonnées de l'unité.
    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    // Vérifie si une coordonnée est dans les limites du plateau.
    // coord : position à tester, maxX/maxY : taille du plateau.
    // Retourne true si coordonnée valide.
    public boolean isCoordinateInBoard(GameElement coord, int maxX, int maxY) {
        return coord.getX() >= INIT_INDEX && coord.getY() >= INIT_INDEX
                && coord.getX() < maxX && coord.getY() < maxY;
    }
    

    // Retourne les cases adjacentes à coord qui sont dans le plateau et non occupées.
    // coord : position centrale, maxX/maxY : dimensions du plateau,
    // occupied : liste des cases occupées.
    public List<GameElement> getAccessibleAdjacentCoordinates(GameElement coord, int maxX, int maxY, List<GameElement> occupied) {
        List<GameElement> result = new ArrayList<>();
        int[] dx = {OFFSET_NEGATIVE, OFFSET_NEGATIVE, OFFSET_NEGATIVE, OFFSET_ZERO, OFFSET_ZERO, OFFSET_POSITIVE, OFFSET_POSITIVE, OFFSET_POSITIVE};
        int[] dy = {OFFSET_NEGATIVE, OFFSET_ZERO, OFFSET_POSITIVE, OFFSET_NEGATIVE, OFFSET_NEGATIVE, OFFSET_NEGATIVE, OFFSET_ZERO, OFFSET_POSITIVE};

        for (int i = INIT_INDEX; i < dx.length; i++) {
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

    // Choisit la meilleure case adjacente pour se rapprocher de la cible.
    // current : position actuelle, target : position cible,
    // maxX/maxY : taille du plateau, occupied : cases occupées.
    // Retourne la case adjacente la plus proche de la cible.
    public GameElement getNextCoordinateForTarget(GameElement current, GameElement target, int maxX, int maxY, List<GameElement> occupied) {
        double minDistance = maxX + maxY;
        GameElement best = new GameElement(current.getX(), current.getY()); // pas de mouvement par défaut
        // On parcourt toutes les cases adjacentes accessibles (non occupées et dans le plateau)
        for (GameElement adj : getAccessibleAdjacentCoordinates(current, maxX, maxY, occupied)) {
            double distance = adj.getDistanceWith(target);
            // Si cette case est plus proche de la cible que toutes les précédentes, on la garde
            if (distance < minDistance) {
                minDistance = distance;
                best = adj;
            }
        }
        // On retourne la meilleure case trouvée (soit une case adjacente, soit la case actuelle)
        return best;
    }

    // méthode pour avancer vers la cible (controller appelle ça)
    // maxX/maxY : dimensions du plateau, occupied : cases occupées.
    // Gère les MagicStone en choisissant une case libre aléatoire si nécessaire.
    public void moveTowardsTarget(int maxX, int maxY, List<GameElement> occupied) {
        if (!hasValidTarget()) return;

        if (hasReachedTarget()) {
            return;
        }
        GameElement current = new GameElement(this.x, this.y);
        GameElement next = getNextCoordinateForTarget(current, target, maxX, maxY, occupied);

        // Vérifier si la case "next" contient une MagicStone
        for (GameElement e : occupied) {
            if (e instanceof MagicStone && e.getX() == next.getX() && e.getY() == next.getY()) {
                // Trouver une case libre aléatoire
                GameElement freeCell = GameElement.getRandomFreeCell(maxX, maxY, occupied);
                if (!freeCell.equals(GameElement.NO_POSITION)) {
                    // déplacer unité sur la case libre
                    setPosition(freeCell.getX(), freeCell.getY());
                    // Ne pas changer la cible, continuer vers la même cible depuis cette nouvelle position
                    return;
                }
            }
        }

        // Sinon, déplacer normalement
        setPosition(next.getX(), next.getY());
    }


    // Définit la cible de l'unité.
    // target : nouvelle cible ou null pour indiquer pas de cible.
    public void setTarget(GameElement target) {
        if (target == null) {
            // NO_POSITION pour signifie qu'il n'y a pas de cible valide
            this.target = GameElement.NO_POSITION;
        } else {
            this.target = target;
        }
    }

    // Cherche la première case libre autour de startPos dans un rayon maxDistance.
    // startPos : point de départ, maxDistance : rayon de recherche,
    // maxX/maxY : dimensions du plateau, occupied : cases occupées.
    // Retourne la coordonnée libre ou NO_POSITION si aucune trouvée.
    public static GameElement findNearestFreeCoordinate(GameElement startPos, int maxDistance, int maxX, int maxY, List<GameElement> occupied) {
        // On cherche une case libre à partir de la position de départ, en partant de la distance minimale
        for (int dist = MIN_DISTANCE; dist <= maxDistance; dist++) {
            for (int dx = -dist; dx <= dist; dx++) {
                int dy = dist - Math.abs(dx);

                int x1 = startPos.getX() + dx;
                int y1 = startPos.getY() + dy;

                // Si cette case est dans le plateau et libre, on la retourne directement
                if (isValidAndFree(x1, y1, maxX, maxY, occupied)) {
                    return new GameElement(x1, y1);
                }

                // Si dy n'est pas zéro, on teste aussi la case symétrique en y
                if (dy != INIT_INDEX) {
                    int x2 = startPos.getX() + dx;
                    int y2 = startPos.getY() - dy;

                    if (isValidAndFree(x2, y2, maxX, maxY, occupied)) {
                        return new GameElement(x2, y2);
                    }
                }
            }
        }
        // Si aucune case libre n'a été trouvée dans le rayon spécifié,
        // on retourne la constante NO_POSITION qui indique une position invalide ou "nulle".
        return GameElement.NO_POSITION;
    }

    // Vérifie si une case (x,y) est valide dans le plateau et non occupée.
    // Retourne true si libre.
    private static boolean isValidAndFree(int x, int y, int maxX, int maxY, List<GameElement> occupied) {
        if (x < INIT_INDEX || x >= maxX || y < INIT_INDEX || y >= maxY) return false;

        // libre si NON occupé
        return !GameElement.isOccupied(x, y, occupied);
    }


    // Retourne une chaîne décrivant l'unité : nom, ville (Nord/Sud) et coordonnées.
    @Override
    public String toString() {
        return String.format("%s (%s, x=%d, y=%d)",
                getClass().getSimpleName(),
                city != null && city.isNorth ? NORTH_LABEL : SOUTH_LABEL,
                x, y);
    }

}
