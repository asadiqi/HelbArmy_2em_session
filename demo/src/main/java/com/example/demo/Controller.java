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
    private List<Tree> trees;
    private List<Stone> stones;

    private static final int INITIAT_INDEX = 0;
    private final int LAST_INDEX_OFFSET = 1;
    private List<GameElement> allElements = new ArrayList<>();
    private double treeRatio=0.05;
    private double StoneRatio=0.03;
    private City northCity;
    private City southCity;


    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones=new ArrayList<Stone>();

        setupCity();
        generateRandomResources("tree");
        generateRandomResources("stone");
        view.initView(this);
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
            //System.out.println("Game loop tick");
            generateCollecter();
            view.drawAllElements();// à corriger

        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }




    private void generateRandomResources(String type) {
        int numberToGenerate;
        if (type.equals("tree")) {
            numberToGenerate = (int) (gridRows * gridCols * treeRatio);
        } else if (type.equals("stone")) {
            numberToGenerate = (int) (gridRows * gridCols * StoneRatio);
        } else {
            return;
        }

        Random rand = new Random();

        while ((type.equals("tree") && trees.size() < numberToGenerate)
                || (type.equals("stone") && stones.size() < numberToGenerate)) {

            int x = rand.nextInt(gridCols);
            int y = rand.nextInt(gridRows);

            if (type.equals("stone")) {
                // Vérifie que x+1 et y+1 sont dans la grille
                if (x + 1 >= gridCols || y + 1 >= gridRows) {
                    continue; // position non valide pour 2x2
                }
                // Vérifie que les 4 cases sont libres
                if (!isOccupied(x, y) && !isOccupied(x + 1, y) && !isOccupied(x, y + 1) && !isOccupied(x + 1, y + 1)) {
                    // On crée une pierre sur la case (x,y) - tu peux modifier la classe Stone si tu veux stocker la zone 2x2
                    Stone stone = new Stone(new GameElement(x, y));
                    stones.add(stone);
                    allElements.add(stone);

                    // Ajoute les 4 cellules dans allElements (pour marquer comme occupées)
                    allElements.addAll(stone.getOccupiedCells());

                    System.out.println("Rochers 2x2 placé en: " + x + " " + y);
                } else {
                    System.out.println("Zone 2x2 occupée à: " + x + " " + y + ", nouvelle tentative...");
                }
            } else if (type.equals("tree")) {
                if (!isOccupied(x, y)) {
                    Tree tree = new Tree(new GameElement(x, y));
                    trees.add(tree);
                    allElements.add(tree);
                    System.out.println("Tree placé en: " + x + " " + y);
                } else {
                    System.out.println("Case occupée: " + x + " " + y + ", nouvelle tentative...");
                }
            }
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


    public void generateCollecter() {

        for (GameElement e : allElements) {
            if (e instanceof City city) {
                if (city.isNorth) {
                    northCity=city;
                } else {
                    southCity=city;
                }
            }
        }


        GameElement northPos = findNearestFreePosition(northCity,14);
        Collecter northCollecter = new Collecter(northPos, true, true, false);
        allElements.add(northCollecter);
        System.out.println("North collecter créé en: " + northPos.getX() + " " + northPos.getY());

        GameElement southPos = findNearestFreePosition(southCity,14);
        Collecter southCollecter = new Collecter(southPos, false, false, true);
        allElements.add(southCollecter);
        System.out.println("South collecter créé en: " + southPos.getX() + " " + southPos.getY());
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
        return new GameElement(-1,-1); // position invalide au lieu de null
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