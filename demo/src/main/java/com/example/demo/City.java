package com.example.demo;


import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import com.example.demo.units.Assassin;
import com.example.demo.units.Collecter;
import com.example.demo.units.Seeder;
import com.example.demo.units.Unit;

import java.util.List;

public class City extends GameElement {

    private static final String northCityFilePath = "img/white/northCity.png";
    private static final String southCityFilePath = "img/black/southCity.png";
    private static final String RESOURCE_TYPE_STONE = "stone";
    private static final String RESOURCE_TYPE_TREE = "tree";
    private static final String NORTH_LIBELLE = "north";
    private static final String SOUTH_LIBELLE = "south";

    private static final double DEFAULT_LUMBERJACK_PROBABILITY = 0.5; // probabilité par défaut pour un Collecter
    private static final double BASE_ASSASSIN_PROBABILITY = 0.1;      // probabilité de base pour générer un Assassin
    private static final double MAX_ASSASSIN_PROBABILITY = 0.9;       // probabilité max pour générer un Assassin
    private static final int RANDOM_UNIT_TYPES = 3;                   // nombre de types d'unités possibles pour génération aléatoire
    private static final double ASSASSIN_ENEMY_DIVISOR = 5.0;         // facteur pour ajuster la probabilité d'Assassin selon ennemis
    private static final int INIT_INDEX = 0;                           // index initial ou valeur de départ pour compteur

    private int stockWood = 0;  // stock pour le bois
    private int stockStone = 0; // stock pour le minerai

    public boolean isNorth;


    // Constructeur de la classe City
    public City(GameElement position, boolean isNorth) {
        super(position.getX(), position.getY());
        this.isNorth = isNorth;
    }

    // Retourne le chemin de l'image de la ville selon son camp (nord ou sud).
    @Override
    public String getImagePath() {
        return isNorth ? northCityFilePath : southCityFilePath;
    }

    // Retourne le stock actuel de bois de la ville.
    public int getStockWood() {
        return stockWood;
    }

    // Ajoute une quantité de bois au stock.
    // amount : quantité à ajouter.
    public void incrementStockWood(int amount) {
        stockWood += amount;
    }

    // Retourne le stock actuel de pierre de la ville.
    public int getStockStone() {
        return stockStone;
    }

    // Ajoute une quantité de pierre au stock.
    // amount : quantité à ajouter.
    public void incrementStockStone(int amount) {
        stockStone += amount;
    }

    // Génère un collecteur en fonction de la proportion de ressources existantes.
    // trees : liste des arbres, stones : liste des pierres,
    // allElements : toutes les entités sur le plateau,
    // gridCols/gridRows : taille du plateau, maxDistance : distance max depuis la ville.
    public void generateCollectorBasedOnResources(List<Tree> trees, List<Stone> stones,
                                                  List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        int totalResources = trees.size() + stones.size();     // Calcul du nombre total de ressources disponibles
        // Probabilité de générer un collecteur de bois (lumberjack) ou de pierre
        double lumberjackProbability = (totalResources == INIT_INDEX) ? DEFAULT_LUMBERJACK_PROBABILITY : (double) trees.size() / totalResources;
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        // Ajouter le collecteur si la position est libre, en décidant si c'est un lumberjack selon la probabilité
        addUnitIfPossible(allElements, pos, new Collecter(pos, this, Math.random() < lumberjackProbability));
    }

    // Génère un semeur qui vise aléatoirement le bois ou la pierre.
    // allElements : toutes les entités sur le plateau,
    // gridCols/gridRows : taille du plateau, maxDistance : distance max depuis la ville.
    public void generateSeederBasedOnResources(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        // Choix aléatoire du type de ressource ciblée par le semeur
        String type = Math.random() < DEFAULT_LUMBERJACK_PROBABILITY ? RESOURCE_TYPE_STONE : RESOURCE_TYPE_TREE;
        // Trouver une position libre autour de la ville
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);
        if (!pos.equals(GameElement.NO_POSITION)) {
            Seeder seeder = new Seeder(pos, this); // Créer le semeur et définir sa ressource cible
            seeder.setTargetRessourceType(type);
            addUnitIfPossible(allElements, pos, seeder);  // Ajouter le semeur à la liste des éléments si possible
        }
    }

    // Génère un assassin en fonction du nombre d'ennemis existants.
    // allElements : toutes les entités sur le plateau,
    // gridCols/gridRows : taille du plateau, maxDistance : distance max depuis la ville.
    // Force la création d'un premier assassin si aucun n'existe.
    public void generateAssassinBasedOnEnemies(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        // Compter les assassins alliés et ennemis
        int assassinsAlly = INIT_INDEX;
        int assassinsEnemy = INIT_INDEX;
        for (GameElement e : allElements) {
            if (e instanceof Assassin a) {
                if (a.getCity().isNorth == this.isNorth) assassinsAlly++;
                else assassinsEnemy++;
            }
        }

        // Probabilité de générer un nouvel assassin en fonction du nombre d'ennemis
        double probGenerate = Math.min(BASE_ASSASSIN_PROBABILITY + (assassinsEnemy / ASSASSIN_ENEMY_DIVISOR), MAX_ASSASSIN_PROBABILITY);
        int totalAssassins = assassinsAlly + assassinsEnemy;

        // Trouver une position libre pour placer l'assassin
        GameElement pos = findPlacementForUnit(allElements, gridCols, gridRows, maxDistance);

        // Forcer le premier assassin si aucun
        if (totalAssassins == INIT_INDEX) {
            if (pos.equals(GameElement.NO_POSITION)) {
                pos = new GameElement(this.getX(), this.getY()); // mettre sur la ville si aucune position libre
            }
            addUnitIfPossible(allElements, pos, new Assassin(pos, this));
            System.out.println("Premier assassin généré de force côté " + (isNorth ?  NORTH_LIBELLE: SOUTH_LIBELLE));
            return;
        }

        // Génération normale selon la probabilité
        if (Math.random() < probGenerate && !pos.equals(GameElement.NO_POSITION)) {
            addUnitIfPossible(allElements, pos, new Assassin(pos, this));
        }
    }

    // Génère aléatoirement un collecteur, semeur ou assassin.
    // Paramètres similaires aux autres méthodes de génération.
    public void generateRandomUnit(List<Tree> trees, List<Stone> stones, List<GameElement> allElements,
                                   int gridCols, int gridRows, int maxDistance) {
        int random = (int) (Math.random() * RANDOM_UNIT_TYPES);
        switch (random) {
            case 0 -> generateCollectorBasedOnResources(trees, stones, allElements, gridCols, gridRows, maxDistance);
            case 1 -> generateSeederBasedOnResources(allElements, gridCols, gridRows, maxDistance);
            case 2 -> generateAssassinBasedOnEnemies(allElements, gridCols, gridRows, maxDistance);
        }
    }

    // Trouve la première case libre proche de la ville pour placer une unité.
    // allElements : toutes les entités sur le plateau,
    // gridCols/gridRows : taille du plateau, maxDistance : rayon de recherche.
    private GameElement findPlacementForUnit(List<GameElement> allElements, int gridCols, int gridRows, int maxDistance) {
        return Unit.findNearestFreeCoordinate(this, maxDistance, gridCols, gridRows, allElements);
    }

    // Ajoute l'unité à la liste allElements si la position est valide.
    // pos : position où placer l'unité, unit : unité à ajouter.
    private void addUnitIfPossible(List<GameElement> allElements, GameElement pos, Unit unit) {
        if (!pos.equals(GameElement.NO_POSITION)) {
            if(unit instanceof Assassin a ) a.city= this ;
            else if ( unit instanceof Collecter c ) c.city = this ;
            else if (unit instanceof Seeder s ) s.city = this;

            allElements.add(unit);
        }
    }

}
