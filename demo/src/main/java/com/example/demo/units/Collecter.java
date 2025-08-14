package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;
import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;

import java.util.List;

public class Collecter extends Unit {

    private final int MAX_ADJACENT_DISTANCE = 1; // Distance max pour être adjacent à une ressource
    private final int NO_POSITION_COORD = -1;    // Coordonnée utilisée pour indiquer qu’il n’y a pas de position (évite d’écrire null)
    private final int BONUS_LUMBERJACK_TREE = 2;    // Bonus pour bûcheron sur un arbre
    private final int BONUS_NORMAL_TREE = 1;    // Bonus pour collecteur normal sur un arbre
    private final int BONUS_MINER_STONE = 3;    // Bonus pour mineur sur une pierre
    private final int BONUS_NORMAL_STONE = 1;    // Bonus pour collecteur normal sur une pierre
    private final int BONUS_NORMAL = 1;    // Bonus par défaut si aucun type précis
    private final int MIN_RESSOURCE_AMOUNT = 0;    // Quantité minimale pour considérer qu’une ressource est épuisée
    private final int MIN_RESSOURCE_Amount = 0;
    private  final String TREE_TYPE = "tree";
    private  final String Stone_TYPE = "stone";
    private static  String LUMBERJACK_LABEL = "Lumberjack";
    private static String MINER_LABEL = "piker";

    public static String northCollecterPath = "img/white/northCollecter.png";
    public static String southCollecterPath = "img/black/southCollecter.png";

    private boolean isLumberjackCollecter;
    public City city; // dans Collecter


    //Constructeur de la classe Collecteur
    public Collecter(GameElement position, City city, boolean isLumberjackCollecter) {
        super(position);
        this.city = city;
        this.isLumberjackCollecter = isLumberjackCollecter;
    }

    // retourne l'image selon la ville
    @Override
    public String getImagePath() {
        return city.isNorth ? northCollecterPath : southCollecterPath;
    }

    // Retourne le bonus de collecte selon type de ressource
    // ressourceType : "tree" ou "stone"
    // retourne : bonus entier
    public int getBonus(String ressourceType) {
        if (ressourceType.equals(TREE_TYPE)) {
            return isLumberjackCollecter ? BONUS_LUMBERJACK_TREE : BONUS_NORMAL_TREE;
        } else if (ressourceType.equals(Stone_TYPE)) {
            return isLumberjackCollecter ? BONUS_NORMAL_STONE : BONUS_MINER_STONE;
        } else {
            return BONUS_NORMAL; // bonus par défaut
        }
    }

    // Cherche la ressource la plus proche et la met en cible
    // trees : liste d'arbres
    // stones : liste de pierres
    public void findNearestResource(List<Tree> trees, List<Stone> stones) {
        // initialise closest à "aucune position"
        GameElement closest = new GameElement(NO_POSITION_COORD, NO_POSITION_COORD);
        double minDistance = Double.MAX_VALUE;

        // vérifie tous les arbres
        for (Tree tree : trees) {
            double dist = tree.getDistanceWith(this);
            if (dist < minDistance) {
                minDistance = dist;
                closest = tree; // met à jour la plus proche
            }
        }

        // vérifie toutes les pierres et leurs cellules
        for (Stone stone : stones) {
            for (GameElement cell : stone.getOccupiedCells()) {
                double dist = cell.getDistanceWith(this);
                if (dist < minDistance) {
                    minDistance = dist;
                    closest = cell;
                }
            }
        }

        setTarget(closest);  // définit la cible du collecteur

    }

    // Tente de collecter si adjacent à une ressource
    // trees : liste d'arbres
    // stones : liste de pierres
    // northCity / southCity : villes des deux camps
    public void collectRessource(List<Tree> trees, List<Stone> stones, City northCity, City southCity) {
        // teste chaque arbre
        for (Tree tree : trees) {
            if (isAdjacentTo(tree) && tree.getCurrentWoodAmount() > MIN_RESSOURCE_Amount) {
                tryCollectFromResource(TREE_TYPE, tree, tree, northCity, southCity);
                return; // stop après collecte
            }
        }

        // teste chaque pierre
        for (Stone stone : stones) {
            for (GameElement cell : stone.getOccupiedCells()) {
                if (isAdjacentTo(cell) && stone.getCurrentMineralAmount() > MIN_RESSOURCE_Amount) {
                    tryCollectFromResource(Stone_TYPE, cell, stone, northCity, southCity);
                    return; // stop après collecte
                }
            }
        }
    }


    // Gère le déplacement et la collecte
    // trees : liste d'arbres
    // stones : liste de pierres
    // northCity / southCity : villes
    // allElements : tous les éléments
    // gridCols / gridRows : taille de la grille
    public void handleCollecter(List<Tree> trees, List<Stone> stones, City northCity, City southCity, List<GameElement> allElements, int gridCols, int gridRows) {
        if (!hasValidTarget() || hasReachedTarget()) { // si pas de cible ou cible atteinte
            findNearestResource(trees, stones); // cherche nouvelle ressource
        }
        moveTowardsTarget(gridCols, gridRows, allElements);// avance vers la cible
        collectRessource(trees, stones, northCity, southCity); // collecte si possible
    }

    // Collecte réellement sur la ressource ciblée
    // resourceType : "tree" ou "stone"
    // targetCell : cellule ciblée
    // resource : l'objet source
    // northCity / southCity : villes des deux camps
    private void tryCollectFromResource(String resourceType, GameElement targetCell, Object resource, City northCity, City southCity) {
        if (isAdjacentTo(targetCell)) { // vérifie si adjacent
            int bonus = getBonus(resourceType); // calcule le bonus
            City city = getCityOfCollecter(northCity, southCity);

            String typeCollecter = isLumberjackCollecter ? LUMBERJACK_LABEL : MINER_LABEL;

            if (TREE_TYPE.equals(resourceType)) {
                Tree tree = (Tree) resource;
                if (tree.getCurrentWoodAmount() > MIN_RESSOURCE_Amount) {
                    tree.decreaseWood(bonus); // diminue bois
                    city.incrementStockWood(bonus);// ajoute à la ville
                    System.out.println(typeCollecter + " (" + (city.isNorth ? "north" : "south") + ") collected " + bonus + " wood, stock: " + city.getStockWood());
                }
            } else if (Stone_TYPE.equals(resourceType)) {
                Stone stone = (Stone) resource;
                if (stone.getCurrentMineralAmount() > MIN_RESSOURCE_Amount) {
                    stone.decreaseMineral(bonus);// diminue pierre
                    city.incrementStockStone(bonus);// ajoute à la ville
                    System.out.println(typeCollecter + " (" + (city.isNorth ? "north" : "south") + ") collected " + bonus + " minerals, stock: " + city.getStockStone());
                }
            }
        }
    }

    // Vérifie si une case est adjacente (<= 1 case)
    // other : élément à tester
    // retourne : vrai si adjacent
    private boolean isAdjacentTo(GameElement other) {
        int dx = Math.abs(this.getX() - other.getX());
        int dy = Math.abs(this.getY() - other.getY());
        return dx <= MAX_ADJACENT_DISTANCE && dy <= MAX_ADJACENT_DISTANCE;
    }


    // Retourne la ville associée au collecteur
    // northCity / southCity : villes des deux camps
    private City getCityOfCollecter(City northCity, City southCity) {
        return city.isNorth ? northCity : southCity;
    }



}
