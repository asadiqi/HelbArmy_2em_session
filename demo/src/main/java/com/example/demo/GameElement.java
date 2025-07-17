package com.example.demo;


import java.util.List;

public class GameElement {
    protected int x;
    protected int y;
    protected double defaultRatio=1.0;

    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getImagePath() {
        return  null;   //justifier car c'est une classe mère et elle ne connais pas encore le chemain d'image de la classe fille
        //si non on pourrait mettre : return "img/default.png";
    }

    public double getWidthRatio() {
        return defaultRatio;
    }

    public double getHeightRatio() {
        return defaultRatio;

    }

    public double getDistanceWith(GameElement other) {
        int dx = other.getX() - this.getX();
        int dy = other.getY() - this.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static boolean isOccupied(int x, int y, List<GameElement> elements) {
        for (GameElement element : elements) {
            if (element.getX() == x && element.getY() == y) {
                return false;
            }
        }
        return true;
    }
    public static boolean isFree(int x, int y, List<GameElement> elements) {
        for (GameElement element : elements) {
            if (element.getX() == x && element.getY() == y) {
                return false; // occupé
            }
        }
        return true; // libre
    }



}
