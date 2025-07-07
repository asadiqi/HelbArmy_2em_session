package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;
import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;

import java.util.List;

public class Collecter extends Unit {

    public static String northCollecterPath = "img/white/northCollecter.png";
    public static String southCollecterPath = "img/black/southCollecter.png";
    private boolean isNorthCollecter;
    private boolean isLumberjackCollecter;


    public Collecter(GameElement position, boolean isNorthCollecter, boolean isLumberjackCollecter) {
        super(position);
        this.isNorthCollecter = isNorthCollecter;
        this.isLumberjackCollecter = isLumberjackCollecter;
    }


    @Override
    public String getImagePath() {
        return isNorthCollecter ? northCollecterPath : southCollecterPath;
    }

    public int getBonus(String ressourceType) {
        // ressourceType=ressourceType.toLowerCase();
        if (ressourceType.equals("tree")) {
            if (isLumberjackCollecter) {
                return 2;
            } else {
                return 1;
            }
        } else if (ressourceType.equals("stone")) {
            if (!isLumberjackCollecter) { // si c'est pas un bouchron donc c'est un piocheur
                return 3;
            } else {
                return 1;
            }

        } else {
            return 1;
        }
    }

    public void findNearestResource(List<Tree> trees, List<Stone> stones) {
        GameElement closest = new GameElement(-1, -1);
        double minDistance = Double.MAX_VALUE;

        for (Tree tree : trees) {
            double dist = tree.getDistanceWith(this);
            if (dist < minDistance) {
                minDistance = dist;
                closest = tree;
            }
        }

        for (Stone stone : stones) {
            for (GameElement cell : stone.getOccupiedCells()) {
                double dist = cell.getDistanceWith(this);
                if (dist < minDistance) {
                    minDistance = dist;
                    closest = cell;
                }
            }
        }

        setTarget(closest);
    }

    public void collectRessource(List<Tree> trees, List<Stone> stones, City northCity, City southCity) {
        for (Tree tree : trees) {
            if (isAdjacentTo(tree) && tree.getCurrentWoodAmount() > 0) {
                tryCollectFromTree(tree, northCity, southCity);
                return;
            }
        }

        for (Stone stone : stones) {
            for (GameElement cell : stone.getOccupiedCells()) {
                if (isAdjacentTo(cell) && stone.getCurrentMineralAmount() > 0) {
                    tryCollectFromStone(stone, cell, northCity, southCity);
                    return;
                }
            }
        }
    }

    private void tryCollectFromTree(Tree tree, City northCity, City southCity) {
        if (isAdjacentTo(tree) && tree.getCurrentWoodAmount() > 0) {
            tree.decreaseWood(1);
            City city = getCityOfCollecter(northCity, southCity);
            city.incrementStock(1);
            System.out.println("Collecteur " + (isNorthCollecter ? "nord" : "sud") +
                    " a récolté 1 bois, stock : " + city.getStock());
        }
    }

    private void tryCollectFromStone(Stone stone, GameElement cell, City northCity, City southCity) {
        if (isAdjacentTo(cell) && stone.getCurrentMineralAmount() > 0) {
            stone.decreaseMineral(1);
            City city = getCityOfCollecter(northCity, southCity);
            city.incrementStock(1);
            System.out.println("Collecteur " + (isNorthCollecter ? "nord" : "sud") +
                    " a récolté 1 minerai, stock : " + city.getStock());
        }
    }




    private boolean isAdjacentTo(GameElement other) {
        int dx = Math.abs(this.getX() - other.getX());
        int dy = Math.abs(this.getY() - other.getY());
        return dx <= 1 && dy <= 1;
    }


    private City getCityOfCollecter(City northCity, City southCity) {
        return isNorthCollecter ? northCity : southCity;
    }



}
