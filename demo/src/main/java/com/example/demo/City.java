package com.example.demo;


public class City extends GameElement {

    private static final String northCityFilePath = "img/white/northCity.png";
    private static final String southCityFilePath = "img/black/southCity.png";

    public boolean isNorth;
    private int stock = 0;

    public City(GameElement position, boolean isNorth) {
        super(position.getX(), position.getY());
        this.isNorth = isNorth;
    }

    @Override
    public String getImagePath() {
        return isNorth ? northCityFilePath : southCityFilePath;
    }

    public int getStock() {
        return stock;
    }

    public void incrementStock(int amount) {
        stock += amount;
    }

}
