package com.example.demo;

import com.example.demo.ressource.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {

    private View view;
    private int gridRows = 4;
    private int gridCols = 4;
    private List<Tree> trees;

    private static final int INITIAT_INDEX = 0;
    private final int LAST_INDEX_OFFSET = 1;
    private List<GameElement> allElements = new ArrayList<>();

    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();

        setupCity();
        generateRandomTrees();;
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

    private void generateRandomTrees() {
        Random rand = new Random();
        int maxAttempts = 100;
        int attempts = 0;

        while (trees.size() < 5 && attempts < maxAttempts) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);
            attempts++;

            if (!isOccupied(x, y)) {
                Tree tree = new Tree(new GameElement(x, y));
                trees.add(tree);
                allElements.add(tree);
                System.out.println("Tree placé en: " + x + " " + y);
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