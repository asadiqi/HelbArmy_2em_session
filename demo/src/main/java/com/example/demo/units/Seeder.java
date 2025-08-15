package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;
import com.example.demo.collectable.Flag;
import com.example.demo.collectable.MagicStone;
import com.example.demo.ressource.Resource;
import com.example.demo.ressource.Stone;
import com.example.demo.ressource.Tree;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Seeder extends Unit {


    private static final String TREE_TYPE = "tree"; // Types de ressources possibles
    private static final String STONE_TYPE = "stone";
    private final int TREE_INITIAL_WOOD = 0; // Quantités initiales pour les ressources
    private final int STONE_INITIAL_MINERAL = 0;
    private final int TREE_ADJACENT_OFFSET_START = -1;
    private final int TREE_ADJACENT_OFFSET_END = 2;
    private final int STONE_ZONE_START = 0;
    private final int STONE_ZONE_END = 1;
    private final int INIT_INDEX = 0;
    private final int OFFSET_ONE = 1;

    private String targetRessourceType;
    private Tree plantedTree ;
    private Stone plantedStone;

    public static String northSeederPath = "img/white/northSeeder.png";
    public static String southSeederPath = "img/black/southSeeder.png";
    public static final Tree NO_TREE = new Tree(GameElement.NO_POSITION); // Constantes représentant l'absence d'arbre ou de pierre plantée pour éviter les nulls
    public static final Stone NO_STONE = new Stone(GameElement.NO_POSITION); // Constantes représentant l'absence d'arbre ou de pierre plantée


    public City city;

    //Constructeur de la classe Seeder
    public Seeder(GameElement position, City city) {
        super(position);
        this.city = city;
        this.plantedTree = NO_TREE;
        this.plantedStone = NO_STONE;
    }

    // Retourne le chemin de l'image représentant ce Seeder selon sa ville
    @Override
    public String getImagePath() {
        return city.isNorth ? northSeederPath : southSeederPath;
    }


    // Retourne le type de ressource que le Seeder cible actuellement
    public String getTargetRessourceType() {
        return targetRessourceType;
    }

    // Définit le type de ressource que le Seeder doit cibler
    // type : "tree" ou "stone"
    public void setTargetRessourceType(String type) {
        this.targetRessourceType = type;
    }


    // Choisit une cible parmi les ressources disponibles selon le type
    // resourceType : type de ressource à cibler
    // resources : liste des ressources existantes
    // maxX, maxY : dimensions de la grille
    // occupied : positions déjà occupées
    public void chooseTarget(String resourceType, List<? extends Resource> resources, int maxX, int maxY, List<GameElement> occupied) {

        this.targetRessourceType = resourceType;
        if (TREE_TYPE.equals(resourceType)) {
            chooseTreeTarget((List<Tree>) resources, maxX, maxY, occupied);
        } else if (STONE_TYPE.equals(resourceType)) {
            chooseStoneTarget((List<Stone>) resources, maxX, maxY, occupied);
        }
    }

    // Plante un arbre autour de la cible si possible
    // occupied : positions occupées
    // trees : liste des arbres
    // maxX, maxY : dimensions de la grille
    public Tree plantTree(List<GameElement> occupied, List<Tree> trees, int maxX, int maxY) {
        if (!hasValidTarget()) return NO_TREE;

        List<GameElement> freeAdjacent = getAccessibleAdjacentCoordinates(target, maxX, maxY, occupied);

        // Si au moins une case est libre
        if (!freeAdjacent.isEmpty()) {
            GameElement spot = freeAdjacent.get(new Random().nextInt(freeAdjacent.size()));
            plantResourceAt(spot, occupied, trees, true, maxX, maxY);
            return plantedTree;         // Retourne l'arbre planté
        } else {
            System.out.println("No free space around the tree to plant.");
            return NO_TREE;         // Aucun emplacement libre trouvé

        }
    }

    // Plante une pierre si possible
    // occupied : positions occupées
    // stones : liste des pierres
    // gridCols, gridRows : dimensions de la grille
    // retourne la pierre plantée ou NO_STONE si impossible
    public Stone plantStone(List<GameElement> occupied, List<Stone> stones, int gridCols, int gridRows) {
        if (!hasValidTarget()) return NO_STONE;
        plantResourceAt(target, occupied, stones, false, gridCols, gridRows);
        return plantedStone;
    }

    // Vérifie si le Seeder est adjacent à la zone cible
    // retourne true si le Seeder est proche de la cible
    public boolean isAdjacentToTargetZone() {
        // Si le Seeder n'a pas de cible valide, il ne peut pas être adjacent
        if (!hasValidTarget()) return false;

        int tx = target.getX();
        int ty = target.getY();
        // Parcours d'une zone autour de la cible définie par TREE_ADJACENT_OFFSET
        for (int dx = TREE_ADJACENT_OFFSET_START; dx <= TREE_ADJACENT_OFFSET_END; dx++) {
            for (int dy = TREE_ADJACENT_OFFSET_START; dy <= TREE_ADJACENT_OFFSET_END; dy++) {
                if (dx >= STONE_ZONE_START && dx <= STONE_ZONE_END && dy >= STONE_ZONE_START && dy <= STONE_ZONE_END) continue;
                // Vérifie si le Seeder est sur une case adjacente
                if (this.getX() == tx + dx && this.getY() == ty + dy) {
                    return true;
                }
            }
        }
        return false;     // Si aucune case adjacente n'est trouvée, retourne false
    }


    // Gère le comportement du Seeder à chaque tour
    // trees, stones : listes des ressources
    // allElements : toutes les positions occupées
    // gridCols, gridRows : dimensions de la grille
    public void handleSeeder(List<Tree> trees, List<Stone> stones, List<GameElement> allElements, int gridCols, int gridRows) {
        String type = this.getTargetRessourceType();     // Récupère le type de ressource que le Seeder doit gérer
        // Vérifie si le type est un arbre ou une pierre
        boolean isTree = type.equals(TREE_TYPE);
        boolean isStone = type.equals(TREE_TYPE);

        // Si le Seeder a déjà planté une ressource qui n'est pas mature, on ne fait rien
        if (isTree && plantedTree != NO_TREE && !plantedTree.isMature()) return;
        if (isStone && plantedStone != NO_STONE && !plantedStone.isMature()) return;
        // Si l'arbre planté est arrivé à maturité, on réinitialise le Seeder pour qu'il reprenne sa mission
        if (isTree && plantedTree != NO_TREE && plantedTree.isMature()) {
            System.out.println("Seeder resumes its task, planted tree has matured");
            plantedTree = NO_TREE;
            setTarget(GameElement.NO_POSITION);

        }

        if (isStone && plantedStone != NO_STONE && plantedStone.isMature()) {
            System.out.println("Seeder resumes its task, planted stone has matured");
            plantedStone = NO_STONE;
            setTarget(GameElement.NO_POSITION);
        }

        // Si le Seeder n'a pas de cible valide, il en choisit une nouvelle
        if (!hasValidTarget()) {
            chooseTarget(type, isTree ? trees : stones, gridCols, gridRows, allElements);
        }
        // Déplace le Seeder vers sa cible
        moveTowardsTarget(gridCols, gridRows, allElements);

        boolean reached = isTree ? hasReachedTarget() : isAdjacentToTargetZone();

        // Si la cible est atteinte, plante la ressource
        if (reached) {
            if (isTree) {
                Tree planted = plantTree(allElements, trees, gridCols, gridRows);
                plantedTree = planted;
            } else {
                Stone planted = plantStone(allElements, stones, gridCols, gridRows);
                plantedStone = planted;
            }

            // Réinitialise la cible et choisit une nouvelle cible pour continuer le cycle
            setTarget(GameElement.NO_POSITION);
            chooseTarget(type, isTree ? trees : stones, gridCols, gridRows, allElements);
        }
    }

    // Choisit la cible arbre
    // trees : liste des arbres existants
    // maxX, maxY : dimensions de la grille
    // occupied : positions occupées
    private void chooseTreeTarget(List<Tree> trees, int maxX, int maxY, List<GameElement> occupied) {
        Random random = new Random();

        // logique de choix aléatoire si aucun arbre n'existe
        if (trees.isEmpty()) {
            GameElement randomFree = GameElement.getRandomFreeCell(maxX, maxY, occupied);
            if (randomFree != GameElement.NO_POSITION) {
                setTarget(randomFree);
                System.out.println("Seeder " + (city.isNorth ? "north" : "south") +
                        " found no tree, targeting a random free position: (" +
                        randomFree.getX() + ", " + randomFree.getY() + ")");

            } else {
                System.out.println("No free spot found to plant a tree.");
            }
        } else {
            Tree selectedTree = trees.get(random.nextInt(trees.size()));
            setTarget(selectedTree);
            System.out.println("Seeder " + (city.isNorth ? "north" : "south") +
                    " has chosen a tree at position: (" +
                    selectedTree.getX() + ", " + selectedTree.getY() + ")");
        }
    }

    // Plante une ressource à une position donnée
    // position : où planter la ressource
    // occupied : liste des positions déjà occupées
    // resourceList : liste des ressources du même type
    // isTree : true si c'est un arbre, false pour pierre
    // gridCols, gridRows : dimensions de la grille
    private void plantResourceAt(GameElement position, List<GameElement> occupied,
                                 List<? extends Resource> resourceList, boolean isTree,
                                 int gridCols, int gridRows) {

        // Vérifie si la position est déjà occupée par un MagicStone ou un Flag
        for (GameElement e : occupied) {
            if ((e instanceof MagicStone || e instanceof Flag) &&
                    e.getX() == position.getX() && e.getY() == position.getY()) {
                System.out.println("Cannot plant: position occupied by MagicStone or Flag");
                return;
            }
        }

        if (isTree) {
            // Création d'un arbre à la position choisie
            Tree tree = new Tree(position);
            tree.setWoodAmount(TREE_INITIAL_WOOD);
            tree.setGrowing(true);
            ((List<Tree>) resourceList).add(tree);
            occupied.add(tree);
            this.plantedTree = tree;         // Mémorise l'arbre planté pour le suivi du Seeder
            System.out.println("Tree planted at (" + position.getX() + ", " + position.getY() + ") → growth started");
        } else {
            if (position.getX() + OFFSET_ONE >= gridCols || position.getY() + OFFSET_ONE >= gridRows) {
                System.out.println("Cannot plant the stone: area out of grid bounds.");
                return;
            }
            // Vérifie que toutes les cases nécessaires pour la pierre sont libres
            if (GameElement.isOccupied(position.getX(), position.getY(), occupied) ||
                    GameElement.isOccupied(position.getX() + OFFSET_ONE, position.getY(), occupied) ||
                    GameElement.isOccupied(position.getX(), position.getY() + OFFSET_ONE, occupied) ||
                    GameElement.isOccupied(position.getX() + OFFSET_ONE, position.getY() + OFFSET_ONE, occupied)) {
                System.out.println("Cannot plant the stone: area is occupied.");
                return;
            }
            // Vérifie que le Seeder n'est pas dans la zone cible
            if ((this.getX() >= position.getX() && this.getX() <= position.getX() + OFFSET_ONE) &&
                    (this.getY() >= position.getY() && this.getY() <= position.getY() + OFFSET_ONE)) {
                System.out.println("Cannot plant: Seeder is in the target area!");                return;
            }

            // Création de la pierre et initialisation de sa croissance
            Stone stone = new Stone(position);
            stone.setMineralAmount(STONE_INITIAL_MINERAL);
            stone.setGrowing(true);
            ((List<Stone>) resourceList).add(stone);         // Ajout de la pierre aux listes et aux cases occupées
            occupied.add(stone);
            occupied.addAll(stone.getOccupiedCells());
            this.plantedStone = stone;
            System.out.println("Stone planted at (" + position.getX() + ", " + position.getY() + ") → growth started");
        }
    }

    // Choisit la cible pierre (zone 2x2)
    // stones : liste des pierres existantes
    // maxX, maxY : dimensions de la grille
    // occupied : positions occupées
    private void chooseStoneTarget(List<Stone> stones, int maxX, int maxY, List<GameElement> occupied) {
        // On conserve la logique spéciale pour la pierre (2x2)
        double maxTotalDistance = -OFFSET_ONE;
        GameElement bestSpot = null; // On initialise bestSpot à null car on n'a pas encore trouvé de position candidate valide
        // null signifie "aucune position valide trouvée pour l'instant"

        // On crée une copie des positions occupées, puis on enlève ce Seeder pour ne pas bloquer la recherche
        List<GameElement> filtered = new ArrayList<>(occupied);
        filtered.remove(this);

        // On parcourt toutes les positions possibles où la pierre 2x2 pourrait être plantée
        for (int x = INIT_INDEX; x < maxX - OFFSET_ONE; x++) {
            for (int y = INIT_INDEX; y < maxY - OFFSET_ONE; y++) {
                // Vérifie si les 4 cases nécessaires pour la pierre sont libres
                if (!GameElement.isOccupied(x, y, filtered) &&
                        !GameElement.isOccupied(x + OFFSET_ONE, y, filtered) &&
                        !GameElement.isOccupied(x, y + OFFSET_ONE, filtered) &&
                        !GameElement.isOccupied(x + OFFSET_ONE, y + OFFSET_ONE, filtered)) {

                    GameElement center = new GameElement(x, y);
                    double totalDistance = INIT_INDEX;

                    // Calcule la somme des distances entre la position candidate et toutes les pierres existantes
                    for (Stone s : stones) {
                        totalDistance += center.getDistanceWith(s);
                    }
                    // Si cette position est plus éloignée (distance totale plus grande) que toutes celles déjà testées
                    if (totalDistance > maxTotalDistance) {
                        maxTotalDistance = totalDistance;
                        bestSpot = center; // bestSpot n'est plus null, on a trouvé une position candidate
                    }
                }
            }
        }
        // Après la recherche, si bestSpot est toujours null, cela signifie qu'aucune position 2x2 libre n'a été trouvée
        if (bestSpot != null) {
            setTarget(bestSpot);
            System.out.println("Seeder " + (city.isNorth ? "north" : "south") +
                    " has chosen a distant position for a stone: (" +
                    bestSpot.getX() + ", " + bestSpot.getY() + ")");
        } else {
            // Aucune position trouvée, on informe l'utilisateur que la plantation est impossible actuellement
            System.out.println("Seeder " + (city.isNorth ? "north" : "south") +
                    " found no free 2x2 position to plant a stone.");
        }
    }
}
