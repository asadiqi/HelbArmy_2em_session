package com.example.demo.ressource;

import com.example.demo.GameElement;

public class Tree extends GameElement {

    private int woodAmount;
    private static final int DEFULT_WOOD_AMOUNT=50;
    private static final int MAX_WOOD_AMOUNT =100;
    public  static  String treePath = "img/tree.png";

    public Tree(GameElement position) {
        super(position.getX(),position.getY());
        this.woodAmount =DEFULT_WOOD_AMOUNT;
    }

    @Override
    public String getImagePath() {
        return treePath;
    }

    public int getCurrentWoodAmount() {
        return woodAmount;
    }

    public void decreaseWood(int amount) {
        woodAmount = Math.max(0, woodAmount - amount);
    }


    public boolean isDepleted() {
        return woodAmount <= 0;
    }


}
