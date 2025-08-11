package com.example.demo.collectable;

import com.example.demo.GameElement;

import java.util.ArrayList;
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
        List<GameElement> freePositions = new ArrayList<>();
        for (int row = 0; row < gridRows; row++) {
            for (int col = 0; col < gridCols; col++) {
                boolean free = true;
                for (GameElement e : allElements) {
                    if (e.getX() == row && e.getY() == col) {
                        free = false;
                        break;
                    }
                }
                if (free) {
                    freePositions.add(new GameElement(row, col));
                }
            }
        }

        if (!freePositions.isEmpty()) {
            int randomIndex = (int) (Math.random() * freePositions.size());
            allElements.add(new MagicStone(freePositions.get(randomIndex)));
        } else {
            System.out.println("No free position available to create a magic stone.");
        }
    }


}
