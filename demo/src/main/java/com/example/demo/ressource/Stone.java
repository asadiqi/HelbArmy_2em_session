package com.example.demo.ressource;

import com.example.demo.GameElement;

public class Stone extends GameElement {


    private int mineralAmount;
    private static final int DEFULT_MINREAL_AMOUNT=100;
    public  static  String stonePath = "img/stone.png";

    public Stone(GameElement position) {
        super(position.getX(),position.getY());
        this.mineralAmount = DEFULT_MINREAL_AMOUNT;
    }

    @Override
    public String getImagePath() {
        return stonePath;
    }

    public int getCurrentMineralAmount() {
        return mineralAmount;
    }

}
