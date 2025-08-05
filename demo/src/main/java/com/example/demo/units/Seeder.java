package com.example.demo.units;

import com.example.demo.Controller;
import com.example.demo.GameElement;
import com.example.demo.ressource.Resource;
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



    public void chooseTarget(String resourceType, List<? extends Resource> resources,
                             int maxX, int maxY, List<GameElement> occupied) {

        this.targetRessourceType = resourceType;

        if ("tree".equalsIgnoreCase(resourceType)) {
            chooseTreeTarget((List<Tree>) resources, maxX, maxY, occupied);
        } else if ("stone".equalsIgnoreCase(resourceType)) {
            chooseStoneTarget((List<Stone>) resources, maxX, maxY, occupied);
        }
    }

    private List<GameElement> findPositions(String resourceType, int maxX, int maxY, List<GameElement> occupied) {
        List<GameElement> freePositions = new ArrayList<>();
        if ("tree".equalsIgnoreCase(resourceType)) {
            // Recherche des cases libres
            for (int x = 0; x < maxX; x++) {
                for (int y = 0; y < maxY; y++) {
                    if (GameElement.isOccupied(x, y, occupied)) {
                        freePositions.add(new GameElement(x, y));
                    }
                }
            }
        } else if ("stone".equalsIgnoreCase(resourceType)) {
            // Recherche des positions 2x2 libres
            for (int x = 0; x < maxX - 1; x++) {
                for (int y = 0; y < maxY - 1; y++) {
                    if (GameElement.isOccupied(x, y, occupied) &&
                            GameElement.isOccupied(x + 1, y, occupied) &&
                            GameElement.isOccupied(x, y + 1, occupied) &&
                            GameElement.isOccupied(x + 1, y + 1, occupied)) {
                        freePositions.add(new GameElement(x, y));
                    }
                }
            }
        }
        return freePositions;
    }

    private void chooseTreeTarget(List<Tree> trees, int maxX, int maxY, List<GameElement> occupied) {
        Random random = new Random();

        if (trees.isEmpty()) {
            List<GameElement> freePositions = findPositions("tree", maxX, maxY, occupied);
            if (!freePositions.isEmpty()) {
                GameElement randomFree = freePositions.get(random.nextInt(freePositions.size()));
                setTarget(randomFree);
                System.out.println("Seeder " + (isNorthSeeder ? "nord" : "sud") +
                        " n’a trouvé aucun arbre, il cible une position libre aléatoire : (" +
                        randomFree.getX() + ", " + randomFree.getY() + ")");
            }
        } else {
            Tree selectedTree = trees.get(random.nextInt(trees.size()));
            setTarget(selectedTree);
            System.out.println("Seeder " + (isNorthSeeder ? "nord" : "sud") +
                    " a choisi un arbre en position : (" +
                    selectedTree.getX() + ", " + selectedTree.getY() + ")");
        }
    }

    private void chooseStoneTarget(List<Stone> stones, int maxX, int maxY, List<GameElement> occupied) {
        double maxTotalDistance = -1;
        GameElement bestSpot = null;

        List<GameElement> filtered = new ArrayList<>(occupied);
        filtered.remove(this);

        List<GameElement> candidatePositions = findPositions("stone", maxX, maxY, filtered);

        for (GameElement center : candidatePositions) {
            double totalDistance = 0;

            for (Stone s : stones) {
                totalDistance += center.getDistanceWith(s);
            }

            if (totalDistance > maxTotalDistance) {
                maxTotalDistance = totalDistance;
                bestSpot = center;
            }
        }

        if (bestSpot != null) {
            setTarget(bestSpot);
            System.out.println("Seeder " + (isNorthSeeder ? "nord" : "sud") +
                    " a choisi une position éloignée pour une pierre : (" +
                    bestSpot.getX() + ", " + bestSpot.getY() + ")");
        } else {
            System.out.println("Seeder " + (isNorthSeeder ? "nord" : "sud") +
                    " n’a trouvé aucune position 2x2 libre pour planter une pierre.");
        }
    }


    private void plantResourceAt(GameElement position, List<GameElement> occupied, List<? extends Resource> resourceList, boolean isTree, int gridCols, int gridRows) {
        if (isTree) {
            Tree tree = new Tree(position);
            tree.setWoodAmount(0);
            tree.setGrowing(true);
            ((List<Tree>) resourceList).add(tree);
            occupied.add(tree);
            this.plantedTree = tree;
            System.out.println("Arbre planté à (" + position.getX() + ", " + position.getY() + ") → croissance démarrée");
        } else {
            if (position.getX() + 1 >= gridCols || position.getY() + 1 >= gridRows) {
                System.out.println("Impossible de planter la pierre : zone hors de la grille.");
                return;
            }

            if (!GameElement.isOccupied(position.getX(), position.getY(), occupied) ||
                    !GameElement.isOccupied(position.getX() + 1, position.getY(), occupied) ||
                    !GameElement.isOccupied(position.getX(), position.getY() + 1, occupied) ||
                    !GameElement.isOccupied(position.getX() + 1, position.getY() + 1, occupied)) {
                System.out.println("Impossible de planter la pierre : zone occupée.");
                return;
            }

            if ((this.getX() >= position.getX() && this.getX() <= position.getX() + 1) &&
                    (this.getY() >= position.getY() && this.getY() <= position.getY() + 1)) {
                System.out.println("Impossible de planter : le Seeder est dans la zone cible !");
                return;
            }

            Stone stone = new Stone(position);
            stone.setMineralAmount(0);
            stone.setGrowing(true);
            ((List<Stone>) resourceList).add(stone);
            occupied.add(stone);
            occupied.addAll(stone.getOccupiedCells());
            this.plantedStone = stone;
            System.out.println("Pierre plantée à (" + position.getX() + ", " + position.getY() + ") → croissance démarrée");
        }
    }


    public Tree plantTree(List<GameElement> occupied, List<Tree> trees, int maxX, int maxY) {
        if (!hasValidTarget()) return null;

        List<GameElement> freeAdjacent = getAccessibleAdjacentCoordinates(target, maxX, maxY, occupied);

        if (!freeAdjacent.isEmpty()) {
            GameElement spot = freeAdjacent.get(new Random().nextInt(freeAdjacent.size()));
            plantResourceAt(spot, occupied, trees, true, maxX, maxY);
            return plantedTree;
        } else {
            System.out.println("Aucune case libre autour de l’arbre pour planter.");
            return null;
        }
    }


    public Stone plantStone(List<GameElement> occupied, List<Stone> stones, int gridCols, int gridRows) {
        if (!hasValidTarget()) return null;
        plantResourceAt(target, occupied, stones, false, gridCols, gridRows);
        return plantedStone;
    }

    public boolean isAdjacentToTargetZone() {
        if (!hasValidTarget()) return false;

        int tx = target.getX();
        int ty = target.getY();

        // Zone 2x2 : (tx, ty), (tx+1, ty), (tx, ty+1), (tx+1, ty+1)
        // On veut savoir si le Seeder est sur une case autour de cette zone
        for (int dx = -1; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                if (dx >= 0 && dx <= 1 && dy >= 0 && dy <= 1) continue; // Ignorer les 4 cases de la zone
                if (this.getX() == tx + dx && this.getY() == ty + dy) {
                    return true;
                }
            }
        }

        return false;
    }


}