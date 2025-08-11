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

    public static void createFlagIfNone(List<GameElement> allElements, int gridRows, int gridCols) {
        // Vérifier si Flag existe
        for (GameElement e : allElements) {
            if (e instanceof Flag) return;
        }
        GameElement freePos = findRandomFreePosition(allElements, gridRows, gridCols);
        if (!freePos.equals(GameElement.NO_POSITION)) {
            allElements.add(new Flag(freePos));
        } else {
            System.out.println("No free position available to create a Flag.");
        }
    }


}
