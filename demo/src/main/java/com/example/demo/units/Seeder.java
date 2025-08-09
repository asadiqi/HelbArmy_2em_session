package com.example.demo.units;

import com.example.demo.City;
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
    private City city;
    private String targetRessourceType;
    public static final Tree NO_TREE = new Tree(GameElement.NO_POSITION);
    public static final Stone NO_STONE = new Stone(GameElement.NO_POSITION);

    private Tree plantedTree = null;
    private Stone plantedStone = null;


    public Seeder(GameElement position, City city) {
        super(position);
        this.city = city;
    }

    @Override
    public String getImagePath() {
        return city.isNorth ? northSeederPath : southSeederPath;
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

    private void chooseTreeTarget(List<Tree> trees, int maxX, int maxY, List<GameElement> occupied) {
        Random random = new Random();

        if (trees.isEmpty()) {
            GameElement randomFree = GameElement.getRandomFreeCell(maxX, maxY, occupied);
            if (randomFree != GameElement.NO_POSITION) {
                setTarget(randomFree);
                System.out.println("Seeder " + (city.isNorth ? "nord" : "sud") +
                        " n’a trouvé aucun arbre, il cible une position libre aléatoire : (" +
                        randomFree.getX() + ", " + randomFree.getY() + ")");
            } else {
                System.out.println("Aucune case libre trouvée pour planter un arbre.");
            }
        } else {
            Tree selectedTree = trees.get(random.nextInt(trees.size()));
            setTarget(selectedTree);
            System.out.println("Seeder " + (city.isNorth ? "nord" : "sud") +
                    " a choisi un arbre en position : (" +
                    selectedTree.getX() + ", " + selectedTree.getY() + ")");
        }
    }

    private void chooseStoneTarget(List<Stone> stones, int maxX, int maxY, List<GameElement> occupied) {
        // On conserve la logique spéciale pour la pierre (2x2)
        double maxTotalDistance = -1;
        GameElement bestSpot = null; //// On ne peut pas remplacer null car bestSpot doit être une position valide pour calculer les distances correctement


        List<GameElement> filtered = new ArrayList<>(occupied);
        filtered.remove(this);

        for (int x = 0; x < maxX - 1; x++) {
            for (int y = 0; y < maxY - 1; y++) {
                if (!GameElement.isOccupied(x, y, filtered) &&
                        !GameElement.isOccupied(x + 1, y, filtered) &&
                        !GameElement.isOccupied(x, y + 1, filtered) &&
                        !GameElement.isOccupied(x + 1, y + 1, filtered)) {

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
            System.out.println("Seeder " + (city.isNorth ? "nord" : "sud") +
                    " a choisi une position éloignée pour une pierre : (" +
                    bestSpot.getX() + ", " + bestSpot.getY() + ")");
        } else {
            System.out.println("Seeder " + (city.isNorth ? "nord" : "sud") +
                    " n’a trouvé aucune position 2x2 libre pour planter une pierre.");
        }
    }

    private void plantResourceAt(GameElement position, List<GameElement> occupied,
                                 List<? extends Resource> resourceList, boolean isTree,
                                 int gridCols, int gridRows) {
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
            if (GameElement.isOccupied(position.getX(), position.getY(), occupied) ||
                    GameElement.isOccupied(position.getX() + 1, position.getY(), occupied) ||
                    GameElement.isOccupied(position.getX(), position.getY() + 1, occupied) ||
                    GameElement.isOccupied(position.getX() + 1, position.getY() + 1, occupied)) {
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
        if (!hasValidTarget()) return NO_TREE;

        List<GameElement> freeAdjacent = getAccessibleAdjacentCoordinates(target, maxX, maxY, occupied);
        if (!freeAdjacent.isEmpty()) {
            GameElement spot = freeAdjacent.get(new Random().nextInt(freeAdjacent.size()));
            plantResourceAt(spot, occupied, trees, true, maxX, maxY);
            return plantedTree;
        } else {
            System.out.println("Aucune case libre autour de l’arbre pour planter.");
            return NO_TREE;
        }
    }

    public Stone plantStone(List<GameElement> occupied, List<Stone> stones, int gridCols, int gridRows) {
        if (!hasValidTarget()) return NO_STONE;
        plantResourceAt(target, occupied, stones, false, gridCols, gridRows);
        return plantedStone;
    }

    public boolean isAdjacentToTargetZone() {
        if (!hasValidTarget()) return false;

        int tx = target.getX();
        int ty = target.getY();

        for (int dx = -1; dx <= 2; dx++) {
            for (int dy = -1; dy <= 2; dy++) {
                if (dx >= 0 && dx <= 1 && dy >= 0 && dy <= 1) continue;
                if (this.getX() == tx + dx && this.getY() == ty + dy) {
                    return true;
                }
            }
        }
        return false;
    }
}
