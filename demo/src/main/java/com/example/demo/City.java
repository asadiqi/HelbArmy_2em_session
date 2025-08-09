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


    private GameElement findPlacementForUnit(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        return Unit.findNearestFreeCoordinate(this, maxDistance, gridCols, gridRows, allElements);
    }

    private void addUnitIfPossible(List<GameElement> allElements, GameElement pos, Unit unit) {
        if (!pos.equals(GameElement.NO_POSITION)) {
            allElements.add(unit);
        }
    }

    public void generateCollecter(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance, boolean isLumberjack) {
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        addUnitIfPossible(allElements, pos, new Collecter(pos, this, isLumberjack));
    }

    public void generateSeeder(List<GameElement> allElements, int gridCols, int gridRows, String targetType, int maxDistance) {
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        if (!pos.equals(GameElement.NO_POSITION)) {
            Seeder seeder = new Seeder(pos, this);
            seeder.setTargetRessourceType(targetType);
            addUnitIfPossible(allElements, pos, seeder);
        }
    }

    public void generateAssassin(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        addUnitIfPossible(allElements, pos, new Assassin(pos, this));
    }


    public void generateCollectorBasedOnResources(List<Tree> trees, List<Stone> stones,
                                            List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        double lumberjackProbability = calculateLumberjackProbability(trees, stones);
        generateCollecter(allElements, gridCols, gridRows, maxDistance, Math.random() < lumberjackProbability);
    }

    public static double calculateLumberjackProbability(List<Tree> trees, List<Stone> stones) {
        int totalResources = trees.size() + stones.size();
        if (totalResources == 0) return 0.5;
        return (double) trees.size() / totalResources;
    }

    public void generateSeederBasedOnResources(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        String type = Math.random() < 0.5 ? "stone" : "tree";
        generateSeeder(allElements, gridCols, gridRows, type, maxDistance);
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

        if (totalAssassins == 0) {
            generateAssassin(allElements, gridCols, gridRows, maxDistance);
            System.out.println("Premier assassin généré de force côté " + (isNorth ? "nord" : "sud"));
        } else if (Math.random() < probGenerate) {
            generateAssassin(allElements, gridCols, gridRows, maxDistance);
        }
    }





}
