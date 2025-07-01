package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
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
    private double treeRatio=0.03;

    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones=new ArrayList<Stone>();

        setupCity();
        generateRandomTrees();
        generateRandomStones();
        view.initView(this);
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

    private int calculateNumberOfTrees() {
        return (int) (gridRows * gridCols * treeRatio);
    }


    private int calculateNumberOfStones() {
        return (int) (gridRows * gridCols * treeRatio);
    }



    private void generateRandomTrees() {
        int numberOfTrees = calculateNumberOfTrees();

        Random rand = new Random();


        while (trees.size() < numberOfTrees) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (!isOccupied(x, y)) {
                Tree tree = new Tree(new GameElement(x, y));
                trees.add(tree);
               // System.out.println("Bois: "+tree.getCurrentWoodAmount());
                allElements.add(tree);
                System.out.println("Tree placé en: " + x + " " + y);
            } else {
                System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
            }
        }
}



    private void generateRandomStones() {
        int numberOfStones = calculateNumberOfStones();

        Random rand = new Random();



        while (stones.size() < numberOfStones) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (!isOccupied(x, y)) {
                Stone stone = new Stone(new GameElement(x, y));
                stones.add(stone);
                // System.out.println("Bois: "+tree.getCurrentWoodAmount());
                allElements.add(stone);
                System.out.println("rochers placé en: " + x + " " + y);
            } else {
                System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
            }
        }
    }


    public void setupCity() {
        int lastRow = gridRows - LAST_INDEX_OFFSET;
        int lastCol = gridCols - LAST_INDEX_OFFSET;

        City northCity = new City(new GameElement(INITIAT_INDEX, INITIAT_INDEX), true);
        City southCity = new City(new GameElement(lastRow, lastCol), false);

        allElements.add(northCity);
        allElements.add(southCity);

        System.out.println("North city added on cell: "+northCity.getX() + " " + northCity.getY());
        System.out.println("South city added on cell: "+southCity.getX() + " " + southCity.getY());
    }



    private boolean isOccupied(int x, int y) {
        for (GameElement element : allElements) {
            if (element.getX() == x && element.getY() == y) {
                return true;
            }
        }
        return false;
    }


}