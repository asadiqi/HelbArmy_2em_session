package com.example.demo.collectable;

import com.example.demo.GameElement;
import com.example.demo.units.Unit;

import java.util.List;

public class Flag extends GameElement {

    public static String flagImagePath = "img/flag.png"; // chemin de l'image
    public static final Flag NO_FLAG = new Flag(GameElement.NO_POSITION); // drapeau spécial "aucun"

    // Crée un drapeau à une position donnée
    public Flag(GameElement position) {
        super(position.getX(), position.getY());
    }

    // retourne image de flag
    @Override
    public String getImagePath() {
        return flagImagePath;
    }

    // Crée un drapeau si aucun n'existe, retourne NO_FLAG sinon
    // allElements : tous les éléments de la grille
    // gridRows, gridCols : taille de la grille
    // retourne le drapeau créé ou NO_FLAG
    public static Flag createFlagIfNone(List<GameElement> allElements, int gridRows, int gridCols) {
        // vérifie s'il y a déjà un drapeau
        for (GameElement e : allElements)
            if (e instanceof Flag) return NO_FLAG;

        // trouve une cellule libre aléatoire
        GameElement freePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);

        // vérifie qu'il n'y a pas de MagicStone à cette position
        for (GameElement e : allElements)
            if (e.getX() == freePos.getX() && e.getY() == freePos.getY() && e instanceof MagicStone)
                return NO_FLAG;

        // crée et ajoute le drapeau si position valide
        if (!freePos.equals(GameElement.NO_POSITION)) {
            Flag newFlag = new Flag(freePos);
            allElements.add(newFlag);
            return newFlag;
        }

        return NO_FLAG; // pas de place pour le drapeau
    }

    // Retire le drapeau si une unité arrive dessus
    // unit : unité qui arrive
    // allElements : tous les éléments de la grille
    // retourne true si drapeau retiré, false sinon
    public boolean handleUnitArrival(Unit unit, List<GameElement> allElements) {
        // si l'unité est sur la même position que le drapeau
        if (unit.getX() == this.getX() && unit.getY() == this.getY()) {
            allElements.remove(this); // retire le drapeau

            // réinitialise la cible de toutes les unités
            for (GameElement e : allElements)
                if (e instanceof Unit u) u.setTarget(GameElement.NO_POSITION);

            return true; // drapeau retiré
        }

        return false; // drapeau toujours présent
    }
}
