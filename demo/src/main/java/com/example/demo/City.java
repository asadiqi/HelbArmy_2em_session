package com.example.demo;


import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import com.example.demo.units.Unit;

import java.util.List;

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


    public void generateCollecter(List<GameElement> allElements, int gridCols, int gridRows,int maxDistance) {
        GameElement pos = Unit.findNearestFreePosition(this, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Collecter collecter = new Collecter(pos, this, true);
            allElements.add(collecter);
        }
    }

    public void generateSeeder(List<GameElement> allElements, int gridCols, int gridRows, String targetType,int maxDistance) {
        GameElement pos = Unit.findNearestFreePosition(this, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Seeder seeder = new Seeder(pos, this);
            seeder.setTargetRessourceType(targetType);
            allElements.add(seeder);
        }
    }

    public void generateAssassin(List<GameElement> allElements, int gridCols, int gridRows,int maxDistance) {
        GameElement pos = Unit.findNearestFreePosition(this, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Assassin assassin = new Assassin(pos, this);
            allElements.add(assassin);
        }
    }





}
