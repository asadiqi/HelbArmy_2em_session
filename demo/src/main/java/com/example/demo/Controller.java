

package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
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
    private List<GameElement> allElements = new ArrayList<>();
    private double treeRatio=0.05;
    private double stoneRatio=0.03;
    private City northCity;
    private City southCity;
    private static final int GAMELOOP_INERVAL_MS=1000;
    private static final int UNIT_GENRATION_MS=2000;
    private int elapsedTimeMs = 0;



    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones=new ArrayList<Stone>();

        setupCity();
        //generateRandomTrees();
        generateRandomStones();
        view.initView(this);
        Collecter collecter = new Collecter(new GameElement(1,1),true, true);
        Collecter collecter1 = new Collecter(new GameElement(gridRows-1,gridCols-1),false, false);
        //addGameElement(collecter);
        //addGameElement(collecter1);


        Seeder northSeeder = new Seeder(new GameElement(1,1),true); // cible arbre
        Seeder southSeeder = new Seeder(new GameElement(gridRows-2,gridCols-2),false); // cible stone
        northSeeder.setTargetRessourceType("tree");
        southSeeder.setTargetRessourceType("stone");
        addGameElement(northSeeder);
        addGameElement(southSeeder);


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


    private void setupGameLoop() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(GAMELOOP_INERVAL_MS), event -> {
            moveUnits();          // <- ici on fait bouger les unités
            view.drawAllElements();
            elapsedTimeMs+=GAMELOOP_INERVAL_MS;

            if (elapsedTimeMs >= UNIT_GENRATION_MS) {
                //generateCollecter();
                //genrateSeeder();
                elapsedTimeMs=0;
            }


        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private void moveUnits() {

        for (GameElement element : new ArrayList<>(allElements)) {

            if (element instanceof Seeder seeder) {
                if (seeder.isNorthSeeder && "tree".equalsIgnoreCase(seeder.getTargetRessourceType()) && !seeder.hasValidTarget()) {
                    seeder.chooseRandomTreeAsTarget(trees, gridCols, gridRows, allElements);
                }
                seeder.moveTowardsTarget(gridCols, gridRows, allElements);

            }


            if (element instanceof Collecter collecter) {

                if (!collecter.hasValidTarget() || collecter.hasReachedTarget()) {
                    collecter.findNearestResource(trees, stones);
                }

                collecter.moveTowardsTarget(gridCols, gridRows, allElements);
                collecter.collectRessource(trees, stones, northCity, southCity);

                trees.removeIf(tree -> {
                    if (tree.isDepleted()) {
                        allElements.remove(tree);
                        System.out.println("Arbre retiré: " + tree.getX() + "," + tree.getX());
                        return true;
                    }
                    return false;
                });


                stones.removeIf(stone -> {
                    if (stone.isDepleted()) {
                        allElements.remove(stone);
                        allElements.removeAll(stone.getOccupiedCells());
                        System.out.println("Pierre retiré: " + stone.getX() + "," + stone.getX());
                        return true;
                    }
                    return false;
                });

            }
        }
    }




    // Méthode pour ajouter un élément dans allElements (centralisation)
    private void addGameElement(GameElement element) {
        allElements.add(element);
    }


    private void generateRandomTrees() {
        int numberToGenerate = (int) (gridRows * gridCols * treeRatio);
        Random rand = new Random();

        while (trees.size() < numberToGenerate) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (GameElement.isOccupied(x, y, allElements)) {
                Tree tree = new Tree(new GameElement(x, y));
                trees.add(tree);
                addGameElement(tree);
                //System.out.println("Tree placé en: " + x + " " + y);
            } else {
            //    System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
            }
        }
    }

    private void generateRandomStones() {
        int numberToGenerate = (int) (gridRows * gridCols * stoneRatio);
        Random rand = new Random();

        while (stones.size() < numberToGenerate) {
            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (x + 1 >= gridCols || y + 1 >= gridRows) {
                continue; // position non valide pour 2x2
            }

            if (GameElement.isOccupied(x, y, allElements)
                    && GameElement.isOccupied(x + 1, y, allElements)
                    && GameElement.isOccupied(x, y + 1, allElements)
                    && GameElement.isOccupied(x + 1, y + 1, allElements)) {
                Stone stone = new Stone(new GameElement(x, y));
                stones.add(stone);
                addGameElement(stone);
                // On ajoute aussi les cellules occupées par la pierre (zone 2x2)
                allElements.addAll(stone.getOccupiedCells());

             //   System.out.println("Rochers 2x2 placé en: " + x + " " + y);
            } else {
              //  System.out.println("Zone 2x2 occupée à: " + x + " " + y + ", nouvelle tentative...");
            }
        }
    }

    public void generateCollecter() {
        Random random = new Random();
        boolean isLumberjack = random.nextBoolean(); // soit c'est un bouchron soit c'est un piocheur
        createCollecterForCity(northCity, true, isLumberjack);
       // System.out.println("north city added a collecter "+ (isLumberjack ? "bouchron": "piocheur"));


        isLumberjack = random.nextBoolean();
        createCollecterForCity(southCity, false, isLumberjack);
        //System.out.println("south city added a collecter "+ (isLumberjack ? "bouchron": "piocheur"));
    }

    public void genrateSeeder() {


    }

    private void createCollecterForCity(City city, boolean isNorthCollecter, boolean isLumberjackCollecter) {
        GameElement pos = Unit.findNearestFreePosition(city, maxDistance, gridCols, gridRows, allElements);
        if (pos != null) {
            Collecter collecter = new Collecter(pos, isNorthCollecter, isLumberjackCollecter);
            addGameElement(collecter);
          //  System.out.println((city.isNorth ? "North" : "South") + " collecter créé en: " + pos.getX() + " " + pos.getY());

        } else {
            System.out.println("Aucune position libre trouvée pour collecter de la ville " + (city.isNorth ? "nord" : "sud"));
        }
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






}