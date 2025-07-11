package com.example.demo.units;

import com.example.demo.GameElement;
import com.example.demo.ressource.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seeder extends Unit{

    public static String northSeederPath = "img/white/northSeeder.png";
    public static String southSeederPath = "img/black/southSeeder.png";
    public boolean isNorthSeeder;
    private String targetRessourceType;
    private Tree plantedTree = null;

    public Seeder(GameElement position, boolean isNorthSeeder) {
        super(position);
        this.isNorthSeeder = isNorthSeeder;

    }


    @Override
    public String getImagePath() {
        return isNorthSeeder ? northSeederPath : southSeederPath;
    }

    public String getTargetRessourceType() {
        return targetRessourceType;
    }


    public void setTargetRessourceType(String type) {
        this.targetRessourceType = type;
    }
    
    public void chooseRandomTreeAsTarget(List<Tree> trees, int maxX, int maxY, List<GameElement> occupied) {
        if (!isNorthSeeder || !"tree".equalsIgnoreCase(targetRessourceType)) {
            return;
        }

        Random random = new Random();

        if (trees.isEmpty()) {
            // Choisir une position libre aléatoire sur la grille
            List<GameElement> freePositions = new ArrayList<>();

            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    if (GameElement.isOccupied(x, y, occupied)) {
                        freePositions.add(new GameElement(x, y));
                    }
                }
            }

            if (!freePositions.isEmpty()) {
                GameElement randomFree = freePositions.get(random.nextInt(freePositions.size()));
                setTarget(randomFree);

                System.out.println("Seeder nord n’a trouvé aucun arbre, il cible une position libre aléatoire : (" +
                        randomFree.getX() + ", " + randomFree.getY() + ")");
            }

        } else {
            Tree selectedTree = trees.get(random.nextInt(trees.size()));
            setTarget(selectedTree);

            System.out.println("Seeder nord a choisi un arbre en position : (" +
                    selectedTree.getX() + ", " + selectedTree.getY() + ")");
        }
    }









    public Tree plantTree(List<GameElement> occupied, List<Tree> trees, int maxX, int maxY) {
        if (!hasValidTarget()) return null;

        List<GameElement> freeAdjacent = getAccessibleAdjacentCoordinates(target, maxX, maxY, occupied);

        if (!freeAdjacent.isEmpty()) {
            GameElement plantingSpot = freeAdjacent.get(new Random().nextInt(freeAdjacent.size()));
            Tree newTree = new Tree(plantingSpot);
            newTree.setWoodAmount(0);
            newTree.setGrowing(true);
            trees.add(newTree);
            occupied.add(newTree);
            this.plantedTree = newTree;
            System.out.println("Arbre planté à (" + plantingSpot.getX() + ", " + plantingSpot.getY() + ") → croissance démarrée");
            return newTree;  // retourne l'arbre planté
        } else {
            System.out.println("Aucune case libre autour de l’arbre pour planter.");
            return null;  // échec
        }
    }


    public Tree getPlantedTree() {
        return plantedTree;
    }

    public void setPlantedTree(Tree tree) {
        this.plantedTree = tree;
    }



}
