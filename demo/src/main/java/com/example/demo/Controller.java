package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;


public class Controller {

    private View view;
    private int gridRows = 15;
    private int gridCols = 15;
    private List<Tree> trees;
    private List<Stone> stones;
    private static final int INITIAT_INDEX = 0;
    private final int LAST_INDEX_OFFSET = 1;
    private List<GameElement> allElements = new ArrayList<>();
    private double treeRatio = 0.05;
    private double stoneRatio = 0.03;
    private int maxDistance = gridRows - 1;
    private City northCity;
    private City southCity;
    private static final int GAMELOOP_INERVAL_MS = 1000;
    private static final int UNIT_GENRATION_MS = 500;
    private int elapsedTimeMs = 0;


    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones = new ArrayList<Stone>();

        setupCity();
        generateRandomTrees();
        generateRandomStones();
        view.initView(this);

        Collecter collecter = new Collecter(new GameElement(1, 1), northCity, true);
        //addGameElement(collecter);
        Collecter collecter1 = new Collecter(new GameElement(gridRows - 1, gridCols - 1), southCity, false);
        //addGameElement(collecter1);

        Seeder northSeeder = new Seeder(new GameElement(1, 1), northCity);
        northSeeder.setTargetRessourceType("stone");
        //addGameElement(northSeeder);
        Seeder southSeeder = new Seeder(new GameElement(gridRows - 2, gridCols - 2), southCity);
        southSeeder.setTargetRessourceType("tree");
        //addGameElement(southSeeder);


        Assassin northAssassin = new Assassin(new GameElement(1, 1), northCity);
        //addGameElement(northAssassin);
        Assassin southAssassin = new Assassin(new GameElement(gridRows - 2, gridCols - 2), southCity);
        //addGameElement(southAssassin);

        setupGameLoop();
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public List<GameElement> getGameElements() {
        return allElements;
    }

    private void growPlantedTrees() {
        for (Tree tree : trees) {
            tree.growResource();
        }
    }

    private void growPlantedStones() {
        for (Stone stone : stones) {
            stone.growResource();
        }
    }

    private void addGameElement(GameElement element) {
        allElements.add(element);
    }

    public void setupCity() {
        int lastRow = gridRows - LAST_INDEX_OFFSET;
        int lastCol = gridCols - LAST_INDEX_OFFSET;

        northCity = new City(new GameElement(INITIAT_INDEX, INITIAT_INDEX), true);
        southCity = new City(new GameElement(lastRow, lastCol), false);

        allElements.add(northCity);
        allElements.add(southCity);

        // System.out.println("North city added on cell: "+northCity.getX() + " " + northCity.getY());
        // System.out.println("South city added on cell: "+southCity.getX() + " " + southCity.getY());
    }

    private void setupGameLoop() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(GAMELOOP_INERVAL_MS), event -> {
            moveUnits();
            growPlantedStones();
            growPlantedTrees();
            view.drawAllElements();

            elapsedTimeMs += GAMELOOP_INERVAL_MS;
            if (elapsedTimeMs >= UNIT_GENRATION_MS) {
                generateUnits();
                elapsedTimeMs = 0;
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void moveUnits() {
        for (GameElement element : new ArrayList<>(allElements)) {
            if (element instanceof Seeder seeder) {
                seeder.handleSeeder(trees, stones, allElements, gridCols, gridRows);
            } else if (element instanceof Collecter collecter) {
                collecter.handleCollecter(trees, stones, northCity, southCity, allElements, gridCols, gridRows);
                removeDepletedResources();
            } else if (element instanceof Assassin assassin) {
                assassin.handleAssassin(gridCols, gridRows, allElements);
            }
        }
    }

    private void generateUnits() {
        int random = (int) (Math.random() * 3);

        switch(random) {
           //case 0 -> generateCollecters();
            case 1 -> generateSeeders();
            //case 2 -> generateAssassins();
        }
    }

    private void generateCollecters() {
        int totalResources = trees.size() + stones.size();
        double lumberjackProbability = 0.5;

        if (totalResources > 0) {
            lumberjackProbability = (double) trees.size() / totalResources;
        }

        northCity.generateCollecter(allElements, gridCols, gridRows, maxDistance, Math.random() < lumberjackProbability);
        southCity.generateCollecter(allElements, gridCols, gridRows, maxDistance, Math.random() < lumberjackProbability);
    }

    private void generateSeeders() {
        String northType = Math.random() < 0.5 ? "stone" : "tree";
        String southType = Math.random() < 0.5 ? "stone" : "tree";

        northCity.generateSeeder(allElements, gridCols, gridRows, northType, maxDistance);
        southCity.generateSeeder(allElements, gridCols, gridRows, southType, maxDistance);
    }

    private void generateAssassins() {
        int assassinsNorth = 0;
        int assassinsSouth = 0;
        for (GameElement e : allElements) {
            if (e instanceof Assassin a) {
                if (a.getCity().isNorth) assassinsNorth++;
                else assassinsSouth++;
            }
        }

        double maxProbability = 0.9;
        double baseProbability = 0.1;

        double probAssassinNorth = Math.min(baseProbability + (assassinsSouth / 5.0), maxProbability);
        double probAssassinSouth = Math.min(baseProbability + (assassinsNorth / 5.0), maxProbability);

        int totalAssassins = assassinsNorth + assassinsSouth;

        if (totalAssassins == 0) {
            if (Math.random() < 0.5) {
                northCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
                System.out.println("Premier assassin généré de force côté nord");
            } else {
                southCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
                System.out.println("Premier assassin généré de force côté sud");
            }
        } else {
            if (Math.random() < probAssassinNorth) {
                northCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
            }
            if (Math.random() < probAssassinSouth) {
                southCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
            }
        }
    }

    private void removeDepletedResources() {
        trees.removeAll(Tree.removeDepletedTrees(trees, allElements));
        stones.removeAll(Stone.removeDepletedStones(stones, allElements));
    }

    private void generateRandomTrees() {
        Tree.generateTrees(trees, allElements, gridCols,gridRows,treeRatio);
    }

    private void generateRandomStones() {
        Stone.generateStones(stones, allElements, gridCols,gridRows,stoneRatio);
    }

}