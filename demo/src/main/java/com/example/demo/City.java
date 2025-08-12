package com.example.demo;


import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import com.example.demo.units.Unit;

import java.util.List;

public class City extends GameElement {

    private static final String northCityFilePath = "img/white/northCity.png";
    private static final String southCityFilePath = "img/black/southCity.png";
    public boolean isNorth;

    private int stockWood = 0;  // stock pour le bois
    private int stockStone = 0; // stock pour le minerai

    public City(GameElement position, boolean isNorth) {
        super(position.getX(), position.getY());
        this.isNorth = isNorth;
    }

    @Override
    public String getImagePath() {
        return isNorth ? northCityFilePath : southCityFilePath;
    }

    public int getStockWood() {
        return stockWood;
    }

    public void incrementStockWood(int amount) {
        stockWood += amount;
    }

    public int getStockStone() {
        return stockStone;
    }

    public void incrementStockStone(int amount) {
        stockStone += amount;
    }



    private GameElement findPlacementForUnit(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        return Unit.findNearestFreeCoordinate(this, maxDistance, gridCols, gridRows, allElements);
    }

    private void addUnitIfPossible(List<GameElement> allElements, GameElement pos, Unit unit) {
        if (!pos.equals(GameElement.NO_POSITION)) {
            allElements.add(unit);
        }
    }

    public void generateCollectorBasedOnResources(List<Tree> trees, List<Stone> stones,
                                                  List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        int totalResources = trees.size() + stones.size();
        double lumberjackProbability = (totalResources == 0) ? 0.5 : (double) trees.size() / totalResources;
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        addUnitIfPossible(allElements, pos, new Collecter(pos, this, Math.random() < lumberjackProbability));
    }



    public void generateSeederBasedOnResources(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        String type = Math.random() < 0.5 ? "stone" : "tree";
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        if (!pos.equals(GameElement.NO_POSITION)) {
            Seeder seeder = new Seeder(pos, this);
            seeder.setTargetRessourceType(type);
            addUnitIfPossible(allElements, pos, seeder);
        }
    }

    public void generateAssassinBasedOnEnemies(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        int assassinsAlly = 0;
        int assassinsEnemy = 0;
        for (GameElement e : allElements) {
            if (e instanceof Assassin a) {
                if (a.getCity().isNorth == this.isNorth) assassinsAlly++;
                else assassinsEnemy++;
            }
        }

        double maxProbability = 0.9;
        double baseProbability = 0.1;
        double probGenerate = Math.min(baseProbability + (assassinsEnemy / 5.0), maxProbability);

        int totalAssassins = assassinsAlly + assassinsEnemy;

        if (totalAssassins == 0 || Math.random() < probGenerate) {
            GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
            addUnitIfPossible(allElements, pos, new Assassin(pos, this));
            if (totalAssassins == 0) {
                System.out.println("Premier assassin généré de force côté " + (isNorth ? "nord" : "sud"));
            }
        }
    }



    public void generateRandomUnit(List<Tree> trees, List<Stone> stones, List<GameElement> allElements,
                                   int gridCols, int gridRows, int maxDistance) {
        int random = (int) (Math.random() * 3);
        switch (random) {
            case 0 -> generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case 1 -> generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case 2 -> generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
        }
    }




}
