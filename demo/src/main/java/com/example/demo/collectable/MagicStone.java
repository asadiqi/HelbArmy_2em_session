package com.example.demo.collectable;

import com.example.demo.GameElement;
import java.util.List;

public class MagicStone extends GameElement {

    public static String magicStoneImagePath = "img/magicStone.png";// chemin de l'image magicstone

    // Constructeur de la classe MagisStone
    public MagicStone(GameElement position) {
        super(position.getX(), position.getY());

    }

    // retourne image de la pierre magique
    @Override
    public String getImagePath() {
        return magicStoneImagePath;
    }

    // Crée une pierre magique sur une cellule libre aléatoire
    // allElements : tous les éléments de la grille
    // gridRows, gridCols : taille de la grille
    public static void createMagicStoneAtRandomPosition(List<GameElement> allElements, int gridRows, int gridCols) {
        // trouve une cellule libre
        GameElement freePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);

        // vérifie qu'il n'y a pas de drapeau sur cette position
        for (GameElement e : allElements) {
            if (e.getX() == freePos.getX() && e.getY() == freePos.getY() && e instanceof Flag) {
                System.out.println("MagicStone not created: cell occupied by a Flag.");
                return;
            }
        }

        // ajoute la pierre magique si position valide
        if (!freePos.equals(GameElement.NO_POSITION)) {
            allElements.add(new MagicStone(freePos));
        } else {
            System.out.println("No free position available to create a MagicStone.");
        }
    }
}