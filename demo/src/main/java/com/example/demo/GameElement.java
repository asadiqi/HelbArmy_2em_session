package com.example.demo;


public class GameElement {
    protected int x;
    protected int y;

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

}
