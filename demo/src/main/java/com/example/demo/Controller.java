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

    // Constantes du jeu
    private static final int GRID_ROWS = 15;
    private static final int GRID_COLS = 15;
    private static final int INITIAT_INDEX = 0;
    private static final int LAST_INDEX_OFFSET = 1;

    private static final int GAMELOOP_INERVAL_MS = 1000;
    private static final int UNIT_GENRATION_MS = 2000;

    private static final int FLAG_TIMEOUT_SEC = 10;
    private static final int REMOVE_FLAG_CYCLE_COUNT = 1;
    private static final int FLAG_CREATE_DELAY_SEC = 120;
    private static final int FLAG_REMOVE_DELAY_SEC = 130;

    private static final int RANDOM_UNIT_TYPE_COUNT = 3;

    // Vue et gestion du temps
    private View view;
    private Timeline timeline;
    private Timeline flagTimeline;
    private int elapsedTimeMs = 0;

    // Flags et contrôle clavier
    private Flag currentFlag = Flag.NO_FLAG;
    private boolean isKeyPressBlocked = false;

    // Cartes et éléments du jeu
    private List<Tree> trees;
    private List<Stone> stones;
    private List<GameElement> allElements;

    // Ratios pour génération d’éléments
    private double treeRatio = 0.03;
    private double stoneRatio = 0.01;

    // Villes
    private City northCity;
    private City southCity;

    // Distances et limites
    private int maxDistance = GRID_ROWS - 1;


    // Constructeur de Controlleur qui initialise la vue, les villes, les ressources, et lance le jeu
    public Controller(View view) {
        this.view = view;
        this.trees = new ArrayList<Tree>();
        this.stones = new ArrayList<Stone>();
        currentFlag = new Flag(GameElement.NO_POSITION); // flag "inactif" au départ, pas dans la liste
        this.allElements = new ArrayList<>();

        setupCity();            // crée les villes nord et sud
        generateRandomTrees();  // génère arbres aléatoires
        generateRandomStones(); // génère pierres aléatoires
        view.initView(this); // initialise la vue
        setupGameLoop();        // lance la boucle de jeu
        createFlag();          // initialise le système de flag
    }

    // retourne le nombre de lignes de la grille
    public int getGridRows() {
        return GRID_ROWS;
    }

    // retourne le nombre de colonnes de la grille
    public int getGridCols() {
        return GRID_COLS;
    }

    // retourne tous les éléments du jeu
    public List<GameElement> getGameElements() {
        return allElements;
    }

    // Fait pousser toutes les ressources arbres
    private void growPlantedTrees() {
        for (Tree tree : trees) {
            tree.growResource();
        }
    }

    // Crée les villes nord et sud et les ajoute à la liste des éléments
    public void setupCity() {
        int lastRow = GRID_ROWS - LAST_INDEX_OFFSET;
        int lastCol = GRID_COLS - LAST_INDEX_OFFSET;

        northCity = new City(new GameElement(INITIAT_INDEX, INITIAT_INDEX), true);
        southCity = new City(new GameElement(lastRow, lastCol), false);

        allElements.add(northCity);
        allElements.add(southCity);

        System.out.println("North city added on cell: "+northCity.getX() + " " + northCity.getY());
        System.out.println("South city added on cell: "+southCity.getX() + " " + southCity.getY());
    }

    // Affiche le résultat final du jeu
    public void displayResult() {
        // Initialisation des variables pour le bois et le minerai des deux villes
        int boisNord = INITIAT_INDEX;
        int mineraiNord = INITIAT_INDEX;
        int boisSud = INITIAT_INDEX;
        int mineraiSud = INITIAT_INDEX;

        // Récupère le stock de bois et de minerai de la ville nord
        boisNord = northCity.getStockWood();
        mineraiNord = northCity.getStockStone();

        // Récupère le stock de bois et de minerai de la ville sud
        boisSud = southCity.getStockWood();
        mineraiSud = southCity.getStockStone();

        // Affiche les résultats finaux
        System.out.println("=== End of Game Results ===");
        System.out.println("North - Wood: " + boisNord + ", Mineral: " + mineraiNord);
        System.out.println("South - Wood: " + boisSud + ", Mineral: " + mineraiSud);

        // Détermine le gagnant ou si c'est une égalité
        if (boisNord > boisSud && mineraiNord > mineraiSud) {
            System.out.println("North team wins!");
        } else if (boisSud > boisNord && mineraiSud > mineraiNord) {
            System.out.println("South team wins!");
        } else {
            System.out.println("Draw!");
        }
    }

    // Arrête le jeu et affiche le résultat
    public void endGame() {
        timeline.stop();
        flagTimeline.stop();
        isKeyPressBlocked = true;
        displayResult();
    }

    // Gère les pressions de touches du clavier pour déclencher des actions dans le jeu.
    // Paramètre : code - le code de la touche pressée.
    // Selon la touche, cette méthode peut générer des unités, supprimer des éléments,
    // arrêter le jeu, créer un flag ou une pierre magique, etc.
    public void handleKeyPress(KeyCode code) {
        if (isKeyPressBlocked) {
            // Ne rien faire si bloqué
            return;
        }
        switch (code) {
            // Ville nord
            case A -> northCity.generateCollectorBasedOnResources(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case Z -> northCity.generateSeederBasedOnResources(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case E -> northCity.generateAssassinBasedOnEnemies(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case R -> northCity.generateRandomUnit(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);

            // Ville sud
            case W -> southCity.generateCollectorBasedOnResources(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case X -> southCity.generateSeederBasedOnResources(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case C -> southCity.generateAssassinBasedOnEnemies(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            case V -> southCity.generateRandomUnit(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);

            // Supprimer certains types d'unités
            case J -> allElements.removeIf(e -> e instanceof Collecter);
            case K -> allElements.removeIf(e -> e instanceof Seeder);
            case L -> allElements.removeIf(e -> e instanceof Assassin);
            case M -> allElements.removeIf(e -> e instanceof Unit);

            // Réinitialiser le jeu
            case U -> {
                allElements.removeIf(e -> !(e instanceof Unit));
                trees.clear();
                stones.clear();
                northCity=null; // On ne peut pas supprimer un objet de la mémoire sans mettre à null toutes ses références, car Java supprime en mémoire uniquement les objets plus référencés.
                southCity=null; // On ne peut pas supprimer un objet de la mémoire sans mettre à null toutes ses références, car Java supprime en mémoire uniquement les objets plus référencés.
            }

            // Fin du jeu
            case O -> {
                timeline.stop();
                isKeyPressBlocked = true;
                displayResult();
            }

            case I -> addFlagWithTimeout();
            case P -> MagicStone.createMagicStoneAtRandomPosition(allElements, GRID_ROWS, GRID_COLS);

            default -> {}
        }
        view.drawAllElements();   // Redessine tous les éléments après chaque action

    }

    // Fait pousser toutes les ressources pierres
    private void growPlantedStones() {
        for (Stone stone : stones) {
            stone.growResource();
        }
    }

    // Configure la boucle principale du jeu qui se répète indéfiniment
    private void setupGameLoop() {
        timeline = new Timeline(new KeyFrame(Duration.millis(GAMELOOP_INERVAL_MS), event -> {
            moveUnits();           // déplace toutes les unités
            growPlantedStones();   // fait pousser les pierres
            growPlantedTrees();    // fait pousser les arbres
            view.drawAllElements(); // met à jour l'affichage

            elapsedTimeMs += GAMELOOP_INERVAL_MS;
            if (elapsedTimeMs >= UNIT_GENRATION_MS) {
                generateUnits();
                elapsedTimeMs = INITIAT_INDEX;
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Déplace toutes les unités et gère leur comportement selon le flag ou leur type
    private void moveUnits() {
        Flag flag = Flag.NO_FLAG;
        for (GameElement e : allElements) {
            if (e instanceof Flag f) {
                flag = f;
                break;
            }
        }
        // pour chaque unité, gérer le déplacement et les actions
        for (GameElement element : new ArrayList<>(allElements)) {
            if (element instanceof Unit unit) {
                if (flag != Flag.NO_FLAG) {
                    // si pas de cible ou cible atteinte, choisir une cellule libre aléatoire
                    if (!unit.hasValidTarget() || unit.hasReachedTarget()) {
                        GameElement randomFreePos = GameElement.getRandomFreeCell(GRID_COLS, GRID_ROWS, allElements);
                        if (!randomFreePos.equals(GameElement.NO_POSITION)) {
                            unit.setTarget(randomFreePos);
                        }
                    }
                    unit.moveTowardsTarget(GRID_COLS, GRID_ROWS, allElements);

                    // vérifier si le flag est récupéré
                    if (flag.handleUnitArrival(unit, allElements)) {
                        System.err.println("Flag removed by a unit at position: (" + unit.getX() + ", " + unit.getY() + ")");
                        flag = Flag.NO_FLAG;
                    }
                } else {
                    // comportement normal selon le type d'unité
                    if (unit instanceof Seeder seeder) {
                        seeder.handleSeeder(trees, stones, allElements, GRID_COLS, GRID_ROWS);
                    } else if (unit instanceof Collecter collecter) {
                        collecter.handleCollecter(trees, stones, northCity, southCity, allElements, GRID_COLS, GRID_ROWS);
                        removeDepletedResources(); // supprime ressources épuisées
                    } else if (unit instanceof Assassin assassin) {
                        assassin.handleAssassin(GRID_COLS, GRID_ROWS, allElements);
                    }
                }
            }
        }
    }

    // Génère aléatoirement des unités pour les deux villes
    private void generateUnits() {
        // Choisit un type d'unité aléatoire (0=Collecter, 1=Seeder, 2=Assassin)
        int random = (int) (Math.random() * RANDOM_UNIT_TYPE_COUNT);

        switch(random) {
            case 0 -> {
                // Génère des collecteurs pour la ville nord et la ville sud
                northCity.generateCollectorBasedOnResources(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);
                southCity.generateCollectorBasedOnResources(trees, stones, allElements, GRID_COLS, GRID_ROWS, maxDistance);
            }
            case 1 -> {
                // Génère des semeurs pour la ville nord et la ville sud
                northCity.generateSeederBasedOnResources(allElements, GRID_COLS, GRID_ROWS, maxDistance);
                southCity.generateSeederBasedOnResources(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            }
            case 2 -> {
                // Génère des assassins pour la ville nord et la ville sud
                northCity.generateAssassinBasedOnEnemies(allElements, GRID_COLS, GRID_ROWS, maxDistance);
                southCity.generateAssassinBasedOnEnemies(allElements, GRID_COLS, GRID_ROWS, maxDistance);
            }
        }
    }

    // Supprime les arbres et pierres épuisés
    private void removeDepletedResources() {
        trees.removeAll(Tree.removeDepletedTrees(trees, allElements));
        stones.removeAll(Stone.removeDepletedStones(stones, allElements));
    }

    // Génère des arbres aléatoires selon le ratio
    private void generateRandomTrees() {
        Tree.generateTrees(trees, allElements, GRID_COLS,GRID_ROWS,treeRatio);
    }

    // Génère des pierres aléatoires selon le ratio
    private void generateRandomStones() {
        Stone.generateStones(stones, allElements, GRID_COLS,GRID_ROWS,stoneRatio);
    }


    // Ajoute un flag avec touche i sur la carte
    private void addFlagWithTimeout() {
        // Si un flag existe déjà, ne rien faire
        for (GameElement e : allElements) {
            if (e instanceof Flag) {
                return;
            }
        }

        Flag newFlag = Flag.createFlagIfNone(allElements, GRID_ROWS, GRID_COLS);
        if (newFlag == Flag.NO_FLAG) {
            return; // pas de flag créé
        }

        // Créer un timeline pour supprimer le flag après 10 secondes
        Timeline removeFlagTimeline = new Timeline(new KeyFrame(Duration.seconds(FLAG_TIMEOUT_SEC), e -> {
            allElements.remove(newFlag);
            view.drawAllElements();
        }));
        removeFlagTimeline.setCycleCount(REMOVE_FLAG_CYCLE_COUNT);
        removeFlagTimeline.play();

        view.drawAllElements();
    }

    // Crée un flag automatiquement selon un cycle de temps
    private void createFlag() {
        // Crée une Timeline pour gérer l'apparition et la disparition du drapeau
        flagTimeline = new Timeline(
                new KeyFrame(Duration.seconds(FLAG_CREATE_DELAY_SEC ), e -> {
                    // Crée un drapeau si aucun n'existe encore
                    currentFlag = Flag.createFlagIfNone(allElements, GRID_ROWS, GRID_COLS);
                    // Met à jour l'affichage pour montrer le drapeau
                    view.drawAllElements();
                }),
                new KeyFrame(Duration.seconds(FLAG_REMOVE_DELAY_SEC ), e -> {
                    // Supprime le drapeau actuel de la liste des éléments
                    allElements.remove(currentFlag);
                    currentFlag = Flag.NO_FLAG;
                    view.drawAllElements(); // Met à jour l'affichage pour retirer le drapeau
                })
        );

        flagTimeline.setCycleCount(Animation.INDEFINITE);     // La timeline se répète indéfiniment
        flagTimeline.play();
    }
}