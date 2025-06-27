package com.example.demo.ressource;

import com.example.demo.GameElement;

public class Tree {

    private final int woodAmount;
    private final GameElement position;
    public  static  String treePath = "img/tree.png";

    public Tree(GameElement position) {
        this.woodAmount = 50;
        this.position = position;

    }

    public int getWoodAmount() {
        return woodAmount;
    }

    public GameElement getPosition() {
        return position;
    }

    public static String getTreePatch() {
        return treePath;
    }
}
