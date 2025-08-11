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
        GameElement freePos = findRandomFreePosition(allElements, gridRows, gridCols);
        if (!freePos.equals(GameElement.NO_POSITION)) {
            allElements.add(new MagicStone(freePos));
        } else {
            System.out.println("No free position available to create a MagicStone.");
        }
    }



}
