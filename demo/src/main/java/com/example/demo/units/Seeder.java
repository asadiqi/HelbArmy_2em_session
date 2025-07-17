package com.example.demo.units;

import com.example.demo.GameElement;
import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seeder extends Unit {

    public static String northSeederPath = "img/white/northSeeder.png";
    public static String southSeederPath = "img/black/southSeeder.png";
    public boolean isNorthSeeder;
    private String targetRessourceType;
    private Tree plantedTree = null;
    private Stone plantedStone = null;


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

    public Stone getPlantedStone() {
        return plantedStone;
    }

    public void setPlantedStone(Stone stone) {
        this.plantedStone = stone;
    }

    public void setTargetRessourceType(String type) {
        this.targetRessourceType = type;
    }

    public Tree getPlantedTree() {
        return plantedTree;
    }

    public void setPlantedTree(Tree tree) {
        this.plantedTree = tree;
    }

    public void chooseRandomTreeAsTarget(List<Tree> trees, int maxX, int maxY, List<GameElement> occupied) {
        if (!isNorthSeeder || !"tree".equalsIgnoreCase(targetRessourceType)) {
            return;
        }

        Random random = new Random();

        if (trees.isEmpty()) {
            List<GameElement> freePositions = new ArrayList<>();
            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    if (GameElement.isFree(x, y, occupied)) {
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
            return newTree;
        } else {
            System.out.println("Aucune case libre autour de l’arbre pour planter.");
            return null;
        }
    }

    public void chooseFurthestStoneSpot(List<Stone> stones, int maxX, int maxY, List<GameElement> occupied) {
        if (isNorthSeeder || !"stone".equalsIgnoreCase(targetRessourceType)) return;

        double maxTotalDistance = -1;
        GameElement bestSpot = null;

        // Filtrer pour ignorer le Seeder lui-même
        List<GameElement> filtered = new ArrayList<>(occupied);
        filtered.remove(this);

        for (int x = 0; x < maxX - 1; x++) {
            for (int y = 0; y < maxY - 1; y++) {
                if (GameElement.isFree(x, y, filtered) &&
                        GameElement.isFree(x + 1, y, filtered) &&
                        GameElement.isFree(x, y + 1, filtered) &&
                        GameElement.isFree(x + 1, y + 1, filtered)) {

                    GameElement center = new GameElement(x, y);
                    double totalDistance = 0;

                    for (Stone s : stones) {
                        totalDistance += center.getDistanceWith(s);
                    }

                    if (totalDistance > maxTotalDistance) {
                        maxTotalDistance = totalDistance;
                        bestSpot = center;
                    }
                }
            }
        }

        if (bestSpot != null) {
            setTarget(bestSpot);
            System.out.println("Seeder sud a choisi une position éloignée pour une pierre : (" +
                    bestSpot.getX() + ", " + bestSpot.getY() + ")");
        } else {
            System.out.println("Seeder sud n’a trouvé aucune position 2x2 libre pour planter une pierre.");
        }
    }

    public Stone plantStone(List<GameElement> occupied, List<Stone> stones) {
        if (!hasValidTarget()) return null;

        int x = target.getX();
        int y = target.getY();

        List<GameElement> filtered = new ArrayList<>(occupied);
        filtered.remove(this);

        boolean zoneLibre = GameElement.isFree(x, y, filtered) &&
                GameElement.isFree(x + 1, y, filtered) &&
                GameElement.isFree(x, y + 1, filtered) &&
                GameElement.isFree(x + 1, y + 1, filtered);

        if (zoneLibre) {
            Stone stone = new Stone(new GameElement(x, y));
            stone.setMineralAmount(0);
            stone.setGrowing(true);
            stones.add(stone);
            occupied.add(stone);
            occupied.addAll(stone.getOccupiedCells());

            this.plantedStone = stone;  // mémoriser la pierre plantée

            System.out.println("Pierre plantée à (" + x + ", " + y + ") → croissance démarrée");
            return stone;
        }

        System.out.println("Impossible de planter la pierre : zone occupée.");
        return null;
    }
}