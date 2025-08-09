package com.example.demo.ressource;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Tree extends Resource {

    public static String treePath = "img/tree.png";
    private static final int DEFAULT_WOOD_AMOUNT = 50;
    private static final int MAX_WOOD_AMOUNT = 100;

    public Tree(GameElement position) {
        super(position.getX(), position.getY(), DEFAULT_WOOD_AMOUNT, MAX_WOOD_AMOUNT);
    }

    @Override
    public String getImagePath() {
        return treePath;
    }

    public int getCurrentWoodAmount() {
        return amount;
    }

    public void decreaseWood(int value) {
        decreaseAmount(value);
    }

    public void setWoodAmount(int value) {
        setAmount(value);
    }

    @Override
    protected String getResourceName() {
        return "Arbre";
    }

    public static List<Tree> removeDepletedTrees(List<Tree> trees, List<GameElement> allElements) {
        List<Tree> toRemove = new ArrayList<>();
        for (Tree tree : trees) {
            if (tree.removeIfDepleted(allElements)) {
                toRemove.add(tree);
            }
        }
        return toRemove;
    }

    public static void generateTrees(List<Tree> trees, List<GameElement> allElements, int gridCols, int gridRows, double ratio) {
        int numberToGenerate = computeNumberToGenerate(gridCols,gridRows,ratio);
        while (trees.size() < numberToGenerate) {
            GameElement cell = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);
            if (!cell.equals(GameElement.NO_POSITION)) {
                Tree tree = new Tree(cell);
                trees.add(tree);
                allElements.add(tree);
            }

        }
    }
}
