package com.example.demo.collectable;

import com.example.demo.GameElement;
import java.util.List;

public class MagicStone extends GameElement {

    public static String magicStoneImagePath = "img/magicStone.png";

    public MagicStone(GameElement position) {
        super(position.getX(), position.getY());

    }

    @Override
    public String getImagePath() {
        return magicStoneImagePath;
    }

    public static void createMagicStoneAtRandomPosition(List<GameElement> allElements, int gridRows, int gridCols) {
        GameElement freePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);

        // Vérifie si la position contient déjà un Flag
        for (GameElement e : allElements) {
            if (e.getX() == freePos.getX() && e.getY() == freePos.getY() && e instanceof Flag) {
                System.out.println("MagicStone non créé : case occupée par un Flag.");
                return;
            }
        }

        if (!freePos.equals(GameElement.NO_POSITION)) {
            allElements.add(new MagicStone(freePos));
        } else {
            System.out.println("No free position available to create a MagicStone.");
        }
    }
}