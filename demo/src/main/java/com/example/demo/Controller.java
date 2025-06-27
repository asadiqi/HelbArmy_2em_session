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

    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();

        generateRandomTrees();
        view.initView(this);

    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
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
                trees.add(new Tree(new GameElement(x, y)));
                System.out.println("Tree placé en: " + x + " " + y);
            } else {
                System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
            }
        }

    }


    private boolean isOccupied(int x, int y) {
        for (Tree tree : trees) {
            GameElement pos = tree.getPosition();
            if (pos.x == x && pos.y == y) {
                return true;
            }
        }
        return false;
    }



    public String[] getCityFilePaths() {
        return new String[] {
            City.getNorthCityFilePath(),
            City.getSouthCityFilePath()

        };
    }

    public String getTreeImage() {
        return Tree.getTreePatch();
    }

    public List<Tree> getTrees() {
        return trees;
    }



}