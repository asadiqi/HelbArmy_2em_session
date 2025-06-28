package com.example.demo;


public class City extends GameElement {

    private static final String northCityFilePath = "img/white/northCity.png";
    private static final String southCityFilePath = "img/black/southCity.png";

    private boolean isNorth;

    public City(GameElement position, boolean isNorth) {
        super(position.getX(), position.getY());
        this.isNorth = isNorth;
    }

    @Override
    public String getImagePath() {
        return isNorth ? northCityFilePath : southCityFilePath;
    }
}
