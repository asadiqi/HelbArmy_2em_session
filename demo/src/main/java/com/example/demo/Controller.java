

package com.example.demo;

import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Collecter;
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
    private double treeRatio=0.01;
    private double stoneRatio=0.03;
    private City northCity;
    private City southCity;



    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones=new ArrayList<Stone>();

        setupCity();
        //generateRandomTrees();
        generateRandomStones();
        view.initView(this);
        Collecter collecter = new Collecter(new GameElement(1,1),true, true);
        Collecter collecter1 = new Collecter(new GameElement(0,1),true, true);

        addGameElement(collecter);
        addGameElement(collecter1);

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
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
           // generateCollecter();
            moveUnits();          // <- ici tu fais bouger les unités
            view.drawAllElements();
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }


    private void moveUnits() {
        for (GameElement element : allElements) {
            if (element instanceof Collecter) {
                Collecter collecter = (Collecter) element;

                int oldX = collecter.getX();
                int oldY = collecter.getY();

                if (!collecter.hasValidTarget() || collecter.hasReachedTarget()) {
                    collecter.findNearestResource(trees, stones);
                }

                collecter.moveTowardsTarget(gridCols, gridRows, allElements);

                int newX = collecter.getX();
                int newY = collecter.getY();

                System.out.println("Collecter moved from (" + oldX + "," + oldY + ") to (" + newX + "," + newY + ")");
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

            if (!isOccupied(x, y)) {
                Tree tree = new Tree(new GameElement(x, y));
                trees.add(tree);
                addGameElement(tree);
                System.out.println("Tree placé en: " + x + " " + y);
            } else {
                System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
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

            if (!isOccupied(x, y) && !isOccupied(x + 1, y) && !isOccupied(x, y + 1) && !isOccupied(x + 1, y + 1)) {
                Stone stone = new Stone(new GameElement(x, y));
                stones.add(stone);
                addGameElement(stone);
                // On ajoute aussi les cellules occupées par la pierre (zone 2x2)
                allElements.addAll(stone.getOccupiedCells());

                System.out.println("Rochers 2x2 placé en: " + x + " " + y);
            } else {
                System.out.println("Zone 2x2 occupée à: " + x + " " + y + ", nouvelle tentative...");
            }
        }
    }

    public void generateCollecter() {
        Random random = new Random();
        boolean isLumberjack = random.nextBoolean(); // soit c'est un bouchron soit c'est un piocheur
        createCollecterForCity(northCity, true, isLumberjack);
        System.out.println("north city added a collecter "+ (isLumberjack ? "bouchron": "piocheur"));


        isLumberjack = random.nextBoolean();
        createCollecterForCity(southCity, false, isLumberjack);
        //System.out.println("south city added a collecter "+ (isLumberjack ? "bouchron": "piocheur"));
    }

    private void createCollecterForCity(City city, boolean isNorthCollecter, boolean isLumberjackCollecter) {
        GameElement pos = findNearestFreePosition(city, maxDistance);
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

        System.out.println("North city added on cell: "+northCity.getX() + " " + northCity.getY());
        System.out.println("South city added on cell: "+southCity.getX() + " " + southCity.getY());
    }

    private GameElement findNearestFreePosition(GameElement startPos, int maxDistance) {
        for (int dist=1; dist <= maxDistance; dist++) {
            for (int dx = -dist; dx <= dist; dx++) {
                int dy = dist - Math.abs(dx);

                int x1 = startPos.getX() +dx;
                int y1 = startPos.getX()+dy;

                if (isValidAndFree(x1,y1)) {
                    return new GameElement(x1,y1);
                }

                if (dy !=0) {
                    int x2 = startPos.getX()+ dx;
                    int y2 = startPos.getY() -dy ;

                    if (isValidAndFree (x2,y2)) {
                        return new GameElement(x2,y2);
                    }
                }
            }
        }
        return null ; // justifier retun new GameElement(-1,-1); // position invalide au lieu de null
    }

    private boolean isValidAndFree(int x, int y) {
        return x >=0 && x < gridCols && y >=0 && y < gridRows && !isOccupied(x, y);
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