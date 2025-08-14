package com.example.demo;

import com.example.demo.collectable.Flag;
import com.example.demo.collectable.MagicStone;
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
import javafx.scene.input.KeyCode;

public class Controller {

    private View view;
    private int gridRows = 30;
    private int gridCols = 30;
    private List<Tree> trees;
    private List<Stone> stones;
    private static final int INITIAT_INDEX = 0;
    private final int LAST_INDEX_OFFSET = 1;
    private List<GameElement> allElements = new ArrayList<>();
    private double treeRatio = 0.010;
    private double stoneRatio = 0.03;
    private int maxDistance = gridRows - 1;
    private City northCity;
    private City southCity;
    private static final int GAMELOOP_INERVAL_MS = 1000;
    private static final int UNIT_GENRATION_MS = 500;
    private int elapsedTimeMs = 0;
    private Timeline timeline;
    private boolean isKeyPressBlocked = false;
    private Flag currentFlag = Flag.NO_FLAG;
    private Timeline flagTimeline;


    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones = new ArrayList<Stone>();
        currentFlag = new Flag(GameElement.NO_POSITION); // flag "inactif" au départ, pas dans la liste

        setupCity();
        generateRandomTrees();
        generateRandomStones();
        view.initView(this);
        setupGameLoop();
        createFlag();


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

    private void growPlantedTrees() {
        for (Tree tree : trees) {
            tree.growResource();
        }
    }

    private void growPlantedStones() {
        for (Stone stone : stones) {
            stone.growResource();
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

    private void setupGameLoop() {
        timeline = new Timeline(new KeyFrame(Duration.millis(GAMELOOP_INERVAL_MS), event -> {
            moveUnits();
            growPlantedStones();
            growPlantedTrees();
            view.drawAllElements();

            elapsedTimeMs += GAMELOOP_INERVAL_MS;
            if (elapsedTimeMs >= UNIT_GENRATION_MS) {
                generateUnits();
                elapsedTimeMs = 0;
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
    private void moveUnits() {
        Flag flag = Flag.NO_FLAG;
        for (GameElement e : allElements) {
            if (e instanceof Flag f) {
                flag = f;
                break;
            }
        }

        for (GameElement element : new ArrayList<>(allElements)) {
            if (element instanceof Unit unit) {
                if (flag != Flag.NO_FLAG) {
                    if (!unit.hasValidTarget() || unit.hasReachedTarget()) {
                        GameElement randomFreePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);
                        if (!randomFreePos.equals(GameElement.NO_POSITION)) {
                            unit.setTarget(randomFreePos);
                        }
                    }
                    unit.moveTowardsTarget(gridCols, gridRows, allElements);

                    if (flag.handleUnitArrival(unit, allElements)) {
                        System.err.println("Flag retiré par une unité en position : (" + unit.getX() + ", " + unit.getY() + ")");
                        flag = Flag.NO_FLAG;
                    }
                } else {
                    // comportement normal
                    if (unit instanceof Seeder seeder) {
                        seeder.handleSeeder(trees, stones, allElements, gridCols, gridRows);
                    } else if (unit instanceof Collecter collecter) {
                        collecter.handleCollecter(trees, stones, northCity, southCity, allElements, gridCols, gridRows);
                        removeDepletedResources();
                    } else if (unit instanceof Assassin assassin) {
                        assassin.handleAssassin(gridCols, gridRows, allElements);
                    }
                }
            }
        }
    }

    private void generateUnits() {
        int random = (int) (Math.random() * 3);

        switch(random) {
            case 0 -> {
                northCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
                southCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            }
            case 1 -> {
                northCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
                southCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            }
            case 2 -> {
                northCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
                southCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
            }
        }
    }


    private void removeDepletedResources() {
        trees.removeAll(Tree.removeDepletedTrees(trees, allElements));
        stones.removeAll(Stone.removeDepletedStones(stones, allElements));
    }

    private void generateRandomTrees() {
        Tree.generateTrees(trees, allElements, gridCols,gridRows,treeRatio);
    }

    private void generateRandomStones() {
        Stone.generateStones(stones, allElements, gridCols,gridRows,stoneRatio);
    }

    public void afficherResultat() {
        int boisNord = 0;
        int mineraiNord = 0;
        int boisSud = 0;
        int mineraiSud = 0;

        boisNord = northCity.getStockWood();
        mineraiNord = northCity.getStockStone();

        boisSud = southCity.getStockWood();
        mineraiSud = southCity.getStockStone();
        System.out.println("=== End of Game Results ===");
        System.out.println("North - Wood: " + boisNord + ", Mineral: " + mineraiNord);
        System.out.println("South - Wood: " + boisSud + ", Mineral: " + mineraiSud);

        if (boisNord > boisSud && mineraiNord > mineraiSud) {
            System.out.println("Victoire de l'équipe Nord !");
        } else if (boisSud > boisNord && mineraiSud > mineraiNord) {
            System.out.println("Victoire de l'équipe Sud !");
        } else {
            System.out.println("Égalité !");
        }
    }

    public void endGame() {
        timeline.stop();
        flagTimeline.stop();
        isKeyPressBlocked = true;
        afficherResultat();
    }
    private void addFlagWithTimeout() {
        // Si un flag existe déjà, ne rien faire
        for (GameElement e : allElements) {
            if (e instanceof Flag) {
                return;
            }
        }

        Flag newFlag = Flag.createFlagIfNone(allElements, gridRows, gridCols);
        if (newFlag == Flag.NO_FLAG) {
            return; // pas de flag créé
        }

        // Créer un timeline pour supprimer le flag après 10 secondes
        Timeline removeFlagTimeline = new Timeline(new KeyFrame(Duration.seconds(20), e -> {
            allElements.remove(newFlag);
            view.drawAllElements();
        }));
        removeFlagTimeline.setCycleCount(1);
        removeFlagTimeline.play();

        view.drawAllElements();
    }

    private void createFlag() {
        flagTimeline = new Timeline(
                new KeyFrame(Duration.seconds(5000), e -> {
                    currentFlag = Flag.createFlagIfNone(allElements, gridRows, gridCols);
                    view.drawAllElements();
                }),
                new KeyFrame(Duration.seconds(10), e -> {
                    allElements.remove(currentFlag);
                    currentFlag = Flag.NO_FLAG;
                    view.drawAllElements();
                })
        );

        flagTimeline.setCycleCount(Animation.INDEFINITE);
        flagTimeline.play();
    }


    public void handleKeyPress(KeyCode code) {
        if (isKeyPressBlocked) {
            // Ne rien faire si bloqué
            return;
        }
        switch (code) {
            case A -> northCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case Z -> northCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case E -> northCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
            case R -> northCity.generateRandomUnit(trees, stones, allElements, gridCols, gridRows, maxDistance);

            case W -> southCity.generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case X -> southCity.generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case C -> southCity.generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
            case V -> southCity.generateRandomUnit(trees, stones, allElements, gridCols, gridRows, maxDistance);

            case J -> allElements.removeIf(e -> e instanceof Collecter);
            case K -> allElements.removeIf(e -> e instanceof Seeder);
            case L -> allElements.removeIf(e -> e instanceof Assassin);
            case M -> allElements.removeIf(e -> e instanceof Unit);
            case U -> {
                allElements.removeIf(e -> !(e instanceof Unit));
                trees.clear();
                stones.clear();
                northCity=null; // On ne peut pas supprimer un objet de la mémoire sans mettre à null toutes ses références, car Java supprime en mémoire uniquement les objets plus référencés.
                southCity=null; // On ne peut pas supprimer un objet de la mémoire sans mettre à null toutes ses références, car Java supprime en mémoire uniquement les objets plus référencés.
            }
            case O -> {
                    timeline.stop();
                    isKeyPressBlocked = true;
                    afficherResultat();
                }
            case I -> addFlagWithTimeout();
            case P -> MagicStone.createMagicStoneAtRandomPosition(allElements, gridRows, gridCols);

            default -> {}
        }
        view.drawAllElements();
    }
}