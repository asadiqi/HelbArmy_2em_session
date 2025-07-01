package com.example.demo.ressource;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Stone extends GameElement {


    private int mineralAmount;
    private static final int DEFULT_MINREAL_AMOUNT=100;
    public  static  String stonePath = "img/stone.png";
    private double stoneRatio=2.0;

    private List<GameElement> occupiedCells;  // Représente les 4 cases

    public Stone(GameElement position) {
        super(position.getX(),position.getY());
        this.mineralAmount = DEFULT_MINREAL_AMOUNT;

        this.occupiedCells = new ArrayList<>();

        // Ajoute les 4 cases du bloc 2x2
        occupiedCells.add(new GameElement(x, y));
        occupiedCells.add(new GameElement(x + 1, y));
        occupiedCells.add(new GameElement(x, y + 1));
        occupiedCells.add(new GameElement(x + 1, y + 1));
    }

    @Override
    public String getImagePath() {
        return stonePath;
    }

    public int getCurrentMineralAmount() {
        return mineralAmount;
    }



    public List<GameElement> getOccupiedCells() {
        return occupiedCells;
    }

    @Override
    public double getWidthRatio() {
        return stoneRatio;
    }


    @Override
    public double getHeightRatio() {
        return stoneRatio;
    }

}
