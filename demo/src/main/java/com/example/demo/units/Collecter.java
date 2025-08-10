package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;
import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;

import java.util.List;

public class Collecter extends Unit {

    public static String northCollecterPath = "img/white/northCollecter.png";
    public static String southCollecterPath = "img/black/southCollecter.png";
    private boolean isLumberjackCollecter;

    private City city; // dans Collecter

    public Collecter(GameElement position, City city, boolean isLumberjackCollecter) {
        super(position);
        this.city = city;
        this.isLumberjackCollecter = isLumberjackCollecter;
    }


    @Override
    public String getImagePath() {
        return city.isNorth ? northCollecterPath : southCollecterPath;
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
                tryCollectFromResource("tree", tree, tree, northCity, southCity);
                return;
            }
        }

        for (Stone stone : stones) {
            for (GameElement cell : stone.getOccupiedCells()) {
                if (isAdjacentTo(cell) && stone.getCurrentMineralAmount() > 0) {
                    tryCollectFromResource("stone", cell, stone, northCity, southCity);
                    return;
                }
            }
        }
    }

    private void tryCollectFromResource(String resourceType, GameElement targetCell, Object resource, City northCity, City southCity) {
        if (isAdjacentTo(targetCell)) {
            int bonus = getBonus(resourceType);
            City city = getCityOfCollecter(northCity, southCity);

            String typeCollecter = isLumberjackCollecter ? "Bûcheron" : "Piocheur";

            if ("tree".equals(resourceType)) {
                Tree tree = (Tree) resource;
                if (tree.getCurrentWoodAmount() > 0) {
                    tree.decreaseWood(bonus);
                    city.incrementStock(bonus);
                   // System.out.println(typeCollecter + " (" + (city.isNorth ? "nord" : "sud") +") a récolté " + bonus + " bois, stock : " + city.getStock());
                }
            } else if ("stone".equals(resourceType)) {
                Stone stone = (Stone) resource;
                if (stone.getCurrentMineralAmount() > 0) {
                    stone.decreaseMineral(bonus);
                    city.incrementStock(bonus);
                   // System.out.println(typeCollecter + " (" + (city.isNorth ? "nord" : "sud") +") a récolté " + bonus + " minerai, stock : " + city.getStock());
                }
            }
        }
    }




    private boolean isAdjacentTo(GameElement other) {
        int dx = Math.abs(this.getX() - other.getX());
        int dy = Math.abs(this.getY() - other.getY());
        return dx <= 1 && dy <= 1;
    }


    private City getCityOfCollecter(City northCity, City southCity) {
        return city.isNorth ? northCity : southCity;
    }


    public void handleCollecter(List<Tree> trees, List<Stone> stones, City northCity, City southCity, List<GameElement> allElements, int gridCols, int gridRows) {
        if (!hasValidTarget() || hasReachedTarget()) {
            findNearestResource(trees, stones);
        }

        moveTowardsTarget(gridCols, gridRows, allElements);
        collectRessource(trees, stones, northCity, southCity);
    }




}
