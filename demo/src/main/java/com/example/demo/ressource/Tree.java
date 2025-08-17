package com.example.demo.ressource;

import com.example.demo.GameElement;

import java.util.ArrayList;
import java.util.List;

public class Tree extends Resource {

    public static String treePath = "img/tree.png";
    private static final int DEFAULT_WOOD_AMOUNT = 50;    // quantité initiale de bois
    private static final int MAX_WOOD_AMOUNT = 100;       // quantité max
    private String resourceName = "Tree";                 // nom de la ressource


    // Constructeur de la classe Tree
    public Tree(GameElement position) {
        super(position.getX(), position.getY(), DEFAULT_WOOD_AMOUNT, MAX_WOOD_AMOUNT);
    }

    // retourne chemin de l'image
    @Override
    public String getImagePath() {
        return treePath;
    }

    // retourne la quantité actuelle de bois
    public int getCurrentWoodAmount() {
        return amount;
    }

    // diminue la quantité de bois
    // value : valeur à retirer
    public void decreaseWood(int value) {
        decreaseAmount(value);
    }

    // définit la quantité de bois
    // value : nouvelle quantité
    public void setWoodAmount(int value) {
        setAmount(value);
    }

    // retourne le nom de la ressource
    @Override
    protected String getResourceName() {
        return resourceName;
    }

    // Supprime les arbres épuisés
    // trees : liste de tous les arbres
    // allElements : liste de tous les éléments de la grille
    // retourne la liste des arbres supprimés
// Méthode à ajouter ou modifier dans la classe Tree
    public static List<Tree> removeDepletedTrees(List<Tree> trees, List<GameElement> allElements) {
        List<Tree> toRemove = new ArrayList<>();
        for (Tree tree : trees) {
            if (tree.getCurrentWoodAmount() <= 0 && !tree.isGrowing()) {
                toRemove.add(tree);
                allElements.remove(tree);

                System.out.println("Arbre épuisé supprimé à la position (" +
                        tree.getX() + ", " + tree.getY() + ")");
            }
        }
        return toRemove;
    }

    // Génère de nouveaux arbres aléatoirement
    // trees : liste actuelle des arbres
    // allElements : tous les éléments de la grille
    // gridCols, gridRows : taille de la grille
    // ratio : proportion de cases à remplir
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
