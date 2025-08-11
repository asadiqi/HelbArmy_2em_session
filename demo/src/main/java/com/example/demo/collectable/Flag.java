package com.example.demo.collectable;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Flag extends GameElement {

    public static String flagImagePath = "img/flag.png";

    public Flag(GameElement position) {
        super(position.getX(), position.getY());
    }

    @Override
    public String getImagePath() {
        return flagImagePath;
    }

    // Renvoie le drapeau créé ou null si déjà présent ou pas de place
    public static Flag createFlagIfNone(List<GameElement> allElements, int gridRows, int gridCols) {
        for (GameElement e : allElements) {
            if (e instanceof Flag) return null; // déjà un drapeau
        }
        GameElement freePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);
        if (!freePos.equals(GameElement.NO_POSITION)) {
            Flag flag = new Flag(freePos);
            allElements.add(flag);
            return flag;
        } else {
            System.out.println("No free position available to create a Flag.");
            return null;
        }
    }

}
