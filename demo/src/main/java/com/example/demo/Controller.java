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
import java.util.Random;


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
    private City northCity;
    private City southCity;
    private static final int GAMELOOP_INERVAL_MS = 500;
    private static final int UNIT_GENRATION_MS = 1000;
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
        Assassin southAssassin = new Assassin(new GameElement(gridRows - 2, gridCols - 2), southCity);
        //addGameElement(northAssassin);
        addGameElement(southAssassin);

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
                int random = (int) (Math.random() * 2);

                if (random == 0) {
                    //northCity.generateCollecter(allElements, gridCols, gridRows,maxDistance);
                    //southCity.generateCollecter(allElements, gridCols, gridRows,maxDistance);

                    //northCity.generateSeeder(allElements, gridCols, gridRows, "stone", maxDistance);
                    //southCity.generateSeeder(allElements, gridCols, gridRows, "tree", maxDistance);
                } else {
                    //northCity.generateAssassin(allElements, gridCols, gridRows,maxDistance);
                    //southCity.generateAssassin(allElements, gridCols, gridRows,maxDistance);
                }
                elapsedTimeMs = 0;
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void moveUnits() {
        for (GameElement element : new ArrayList<>(allElements)) {
            if (element instanceof Seeder seeder) {
                handleSeeder(seeder);
            } else if (element instanceof Collecter collecter) {
                handleCollecter(collecter);
            } else if (element instanceof Assassin assassin) {
                assassin.update(gridCols, gridRows, allElements);
            }
        }
    }

    private void handleCollecter(Collecter collecter) {
        if (!collecter.hasValidTarget() || collecter.hasReachedTarget()) {
            collecter.findNearestResource(trees, stones);
        }

        collecter.moveTowardsTarget(gridCols, gridRows, allElements);
        collecter.collectRessource(trees, stones, northCity, southCity);

        removeDepletedResources();

    }

    private void handleSeeder(Seeder seeder) {
        String type = seeder.getTargetRessourceType();

        boolean isTree = type.equalsIgnoreCase("tree");
        boolean isStone = type.equalsIgnoreCase("stone");

        if (isTree && seeder.getPlantedTree() != null && !seeder.getPlantedTree().isMature()) return;
        if (isStone && seeder.getPlantedStone() != null && !seeder.getPlantedStone().isMature()) return;

        if (isTree && seeder.getPlantedTree() != null && seeder.getPlantedTree().isMature()) {
            System.out.println("Seeder reprend sa mission, arbre planté arrivé à maturité");
            seeder.setPlantedTree(null);
            seeder.setTarget(null);
        }

        if (isStone && seeder.getPlantedStone() != null && seeder.getPlantedStone().isMature()) {
            System.out.println("Seeder reprend sa mission, pierre plantée arrivée à maturité");
            seeder.setPlantedStone(null);
            seeder.setTarget(null);
        }

        if (!seeder.hasValidTarget()) {
            seeder.chooseTarget(type, isTree ? trees : stones, gridCols, gridRows, allElements);
        }

        seeder.moveTowardsTarget(gridCols, gridRows, allElements);

        boolean reached = isTree ? seeder.hasReachedTarget() : seeder.isAdjacentToTargetZone();

        if (reached) {
            if (isTree) {
                Tree planted = seeder.plantTree(allElements, trees, gridCols, gridRows);
                seeder.setPlantedTree(planted);
            } else {
                Stone planted = seeder.plantStone(allElements, stones, gridCols, gridRows);
                seeder.setPlantedStone(planted);
            }

            seeder.setTarget(null);
            seeder.chooseTarget(type, isTree ? trees : stones, gridCols, gridRows, allElements);
        }
    }





    private void removeDepletedResources() {
        trees.removeAll(Tree.removeDepletedTrees(trees, allElements));
        stones.removeAll(Stone.removeDepletedStones(stones, allElements));
    }

    private void generateResources(List<? extends GameElement> resources, double ratio, boolean isStone) {
        int numberToGenerate = (int) (gridRows * gridCols * ratio);
        Random rand = new Random();

        while (resources.size() < numberToGenerate) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (isStone) {
                // Pour pierre 2x2
                if (x + 1 >= gridCols || y + 1 >= gridRows) {
                    continue;
                }
                if (GameElement.isOccupied(x, y, allElements) &&
                        GameElement.isOccupied(x + 1, y, allElements) &&
                        GameElement.isOccupied(x, y + 1, allElements) &&
                        GameElement.isOccupied(x + 1, y + 1, allElements)) {
                    Stone stone = new Stone(new GameElement(x, y));
                    ((List<Stone>) resources).add(stone);
                    addGameElement(stone);
                    allElements.addAll(stone.getOccupiedCells());
                }
            } else {
                // Pour arbre 1x1
                if (GameElement.isOccupied(x, y, allElements)) {
                    Tree tree = new Tree(new GameElement(x, y));
                    ((List<Tree>) resources).add(tree);
                    addGameElement(tree);
                }
            }
        }
    }

    private void generateRandomTrees() {
        generateResources(trees, treeRatio, false);
    }

    private void generateRandomStones() {
        generateResources(stones, stoneRatio, true);
    }

}


