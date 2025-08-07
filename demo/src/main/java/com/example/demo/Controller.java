

package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import com.example.demo.units.Unit;
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
    private int maxDistance=gridRows-1;
    private List<Tree> trees;
    private List<Stone> stones;

    private static final int INITIAT_INDEX = 0;
    private final int LAST_INDEX_OFFSET = 1;
    private final Random random = new Random();
    private List<GameElement> allElements = new ArrayList<>();
    private double treeRatio=0.05;
    private double stoneRatio=0.03;
    private City northCity;
    private City southCity;
    private static final int GAMELOOP_INERVAL_MS=500;
    private static final int UNIT_GENRATION_MS=1000;
    private int elapsedTimeMs = 0;


    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones=new ArrayList<Stone>();

        setupCity();
        generateRandomTrees();
        generateRandomStones();
        view.initView(this);
        Collecter collecter = new Collecter(new GameElement(1,1),northCity, true);
        Collecter collecter1 = new Collecter(new GameElement(gridRows-1,gridCols-1),southCity, false);
        //addGameElement(collecter);
        //addGameElement(collecter1);


        Seeder northSeeder = new Seeder(new GameElement(1,1),northCity); // cible arbre
        Seeder southSeeder = new Seeder(new GameElement(gridRows-2,gridCols-2),southCity); // cible stone
        northSeeder.setTargetRessourceType("stone");
        southSeeder.setTargetRessourceType("tree");
        //addGameElement(northSeeder);
        //addGameElement(southSeeder);


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


    // Méthode pour ajouter un élément dans allElements (centralisation)
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
            moveUnits();          // <- ici on fait bouger les unités
            growPlantedStones();
            growPlantedTrees();
            view.drawAllElements();
            elapsedTimeMs+=GAMELOOP_INERVAL_MS;

            if (elapsedTimeMs >= UNIT_GENRATION_MS) {

              int random = (int) (Math.random()*2);

              if (random == 0) {
                  //generateCollecter();
                  generateSeeder();
                  //generateAssassin();

              } else {
                  //genrateSeeder();
                 // generateAssassin();
              }

                elapsedTimeMs=0;
            }


        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void growPlantedTrees() {
        for (Tree tree: trees) {
            if (tree.isGrowing() && !tree.isMature()) {
                int before = tree.getCurrentWoodAmount();
                tree.grow(20);
                int after = tree.getCurrentWoodAmount();

                System.out.println("Arbre en ( "+ tree.getX()+ ", "+tree.getY()+ " bois total : "+ (after - before) + " bois total : "+after);

                if (tree.isMature()) {
                    tree.setGrowing(false);
                    System.out.println("Arbre à ( "+ tree.getX()+", "+tree.getY()+ " est maintenant mature ");
                }
            }
        }
    }

    private void growPlantedStones() {
        for (Stone stone : stones) {
            if (stone.isGrowing() && !stone.isMature()) {
                int before = stone.getCurrentMineralAmount();
                stone.grow(20);  // valeur de croissance par tick (ajustable)
                int after = stone.getCurrentMineralAmount();
                System.out.println("Pierre à (" + stone.getX() + ", " + stone.getY() + ") a grandi de " + (after - before) + " → total: " + after);

                if (stone.isMature()) {
                    stone.setGrowing(false);
                    System.out.println("Pierre à (" + stone.getX() + ", " + stone.getY() + ") est maintenant mature !");
                }
            }
        }
    }

    private void moveUnits() {
        for (GameElement element : new ArrayList<>(allElements)) {
            if (element instanceof Seeder seeder) {
                handleSeeder(seeder);
            } else if (element instanceof Collecter collecter) {
                handleCollecter(collecter);
            } else if (element instanceof  Assassin assassin) {
                handleAssassin(assassin);
            }
        }
    }


    private void handleCollecter(Collecter collecter) {
        if (!collecter.hasValidTarget() || collecter.hasReachedTarget()) {
            collecter.findNearestResource(trees, stones);
        }

        collecter.moveTowardsTarget(gridCols, gridRows, allElements);
        collecter.collectRessource(trees, stones, northCity, southCity);

        removeDepletedResources("tree");
        removeDepletedResources("stone");
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

    private void handleAssassin(Assassin assassin) {
        if (!assassin.hasValidTarget() || assassin.hasReachedTarget()) {
            Assassin closesEnemy = findClosesEnemyAssassin(assassin);
            if (closesEnemy != null) {
                assassin.setTarget(new GameElement(closesEnemy.getX(), closesEnemy.getY()));
            } else {
                assassin.setTarget(null);
            }
        }

        assassin.moveTowardsTarget(gridCols, gridRows, allElements);
    }

    private Assassin findClosesEnemyAssassin (Assassin assassin) {
        double minDistance = Double.MAX_VALUE;
        Assassin closest = null;

        for (GameElement element : allElements) {
            if (element instanceof Assassin other && other != assassin && other.city.isNorth != assassin.city.isNorth ) {
                double distance = assassin.getDistanceWith(other);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest =other;
                }
            }
        }
        return closest;
    }



    private void removeDepletedResources(String resourceType) {
        if ("tree".equalsIgnoreCase(resourceType)) {
            List<Tree> toRemove = new ArrayList<>();
            for (Tree tree : trees) {
                if (tree.isDepleted()) {
                    allElements.remove(tree);
                    System.out.println("Arbre retiré: " + tree.getX() + "," + tree.getY());
                    toRemove.add(tree);
                }
            }
            trees.removeAll(toRemove);
        } else if ("stone".equalsIgnoreCase(resourceType)) {
            List<Stone> toRemove = new ArrayList<>();
            for (Stone stone : stones) {
                if (stone.isDepleted()) {
                    allElements.remove(stone);
                    allElements.removeAll(stone.getOccupiedCells());
                    System.out.println("Pierre retirée: " + stone.getX() + "," + stone.getY());
                    toRemove.add(stone);
                }
            }
            stones.removeAll(toRemove);
        }
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
                    ((List<Stone>)resources).add(stone);
                    addGameElement(stone);
                    allElements.addAll(stone.getOccupiedCells());
                }
            } else {
                // Pour arbre 1x1
                if (GameElement.isOccupied(x, y, allElements)) {
                    Tree tree = new Tree(new GameElement(x, y));
                    ((List<Tree>)resources).add(tree);
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



    public void generateCollecter() {
        spawnCollecter(northCity, random.nextBoolean());
        spawnCollecter(southCity, random.nextBoolean());
    }

    public void generateSeeder() {
        spawnSeeder(northCity, random.nextBoolean() ? "tree" : "stone");
        spawnSeeder(southCity, random.nextBoolean() ? "tree" : "stone");
    }

    public void generateAssassin() {
        spawnAssassin(northCity);
        spawnAssassin(southCity);
    }



    private void spawnCollecter(City city, boolean isLumberjack) {
        GameElement pos = Unit.findNearestFreePosition(city, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Collecter collecter = new Collecter(pos, city, isLumberjack);
            addGameElement(collecter);
        } else {
            System.out.println("Aucune position libre pour collecter de la ville " + (city.isNorth ? "nord" : "sud"));
        }
    }

    private void spawnSeeder(City city, String targetResourceType) {
        GameElement pos = Unit.findNearestFreePosition(city, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Seeder seeder = new Seeder(pos, city);
            seeder.setTargetRessourceType(targetResourceType);
            addGameElement(seeder);
        } else {
            System.out.println("Aucune position libre pour seeder de la ville " + (city.isNorth ? "nord" : "sud"));
        }
    }

    private void spawnAssassin(City city) {
        GameElement pos = Unit.findNearestFreePosition(city, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Assassin assassin = new Assassin(pos, city);
            addGameElement(assassin);
        } else {
            System.out.println("Aucune position libre pour assassin de la ville " + (city.isNorth ? "nord" : "sud"));
        }
    }


}