package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import com.example.demo.units.Unit;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.input.KeyCode;




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
            case 0 -> {
             //   northCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
               // southCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            }
            case 1 -> {
               // northCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
               // southCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            }
            case 2 -> {
               // northCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
               // southCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
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

    public void handleKeyPress(KeyCode code) {
        switch (code) {
            case A -> northCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case Z -> northCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case E -> northCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
            case R -> northCity.generateRandomUnit(trees, stones, allElements, gridCols, gridRows, maxDistance);

            case W -> southCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case X -> southCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case C -> southCity.generateAssassin(allElements, gridCols, gridRows, maxDistance);
            case V -> southCity.generateRandomUnit(trees, stones, allElements, gridCols, gridRows, maxDistance);

            case J -> allElements.removeIf(e -> e instanceof Collecter);
            case K -> allElements.removeIf(e -> e instanceof Seeder);
            case L -> allElements.removeIf(e -> e instanceof Assassin);
            case M -> allElements.removeIf(e -> e instanceof Unit);
            case U -> allElements.removeIf(e -> !(e instanceof Unit));

            default -> {}
        }
        view.drawAllElements();
    }


}