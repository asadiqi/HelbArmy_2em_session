package com.example.demo.units;

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

}
