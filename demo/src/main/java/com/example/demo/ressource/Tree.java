package com.example.demo.ressource;

import com.example.demo.GameElement;

public class Tree extends Resource {

    public static String treePath = "img/tree.png";
    private static final int DEFAULT_WOOD_AMOUNT = 50;
    private static final int MAX_WOOD_AMOUNT = 100;

    public Tree(GameElement position) {
        super(position.getX(), position.getY(), DEFAULT_WOOD_AMOUNT, MAX_WOOD_AMOUNT);
    }

    @Override
    public String getImagePath() {
        return treePath;
    }

    public int getCurrentWoodAmount() {
        return amount;
    }

    public void decreaseWood(int value) {
        decreaseAmount(value);
    }

    public void setWoodAmount(int value) {
        setAmount(value);
    }

    @Override
    protected String getResourceName() {
        return "Arbre";
    }


}
