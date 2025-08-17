    package com.example.demo.ressource;

    import com.example.demo.GameElement;
    import java.util.ArrayList;
    import java.util.List;

    public class Stone extends Resource {

        private static String stonePath = "img/stone.png";
        private static final int DEFAULT_MINERAL_AMOUNT = 100; // quantité initiale
        private static final int MAX_MINERAL_AMOUNT = 200;     // quantité max

        private double stoneRatio = 2.0;                       // ratio largeur/hauteur pour affichage
        private List<GameElement> occupiedCells;               // cellules occupées par la pierre
        private static final int ADJACENT_CELL_OFFSET  = 1;             // décalage pour cellules adjacentes



        // Constructeur de la classe Stone
        public Stone(GameElement position) {
            super(position.getX(), position.getY(), DEFAULT_MINERAL_AMOUNT, MAX_MINERAL_AMOUNT);

            // définit les cellules occupées par la pierre (2x2)
            this.occupiedCells = new ArrayList<>();
            occupiedCells.add(new GameElement(x, y));
            occupiedCells.add(new GameElement(x + ADJACENT_CELL_OFFSET, y));
            occupiedCells.add(new GameElement(x, y + ADJACENT_CELL_OFFSET));
            occupiedCells.add(new GameElement(x + ADJACENT_CELL_OFFSET, y + ADJACENT_CELL_OFFSET));
        }

        // retourne chemin de l'image
        @Override
        public String getImagePath() {
            return stonePath;
        }

        // retourne ratio largeur pour affichage
        @Override
        public double getWidthRatio() {
            return stoneRatio;
        }

        // retourne ratio hauteur pour affichage
        @Override
        public double getHeightRatio() {
            return stoneRatio;
        }

        // retourne la quantité actuelle de minerai
        public int getCurrentMineralAmount() {
            return amount;
        }

        // diminue la quantité de minerai
        // value : valeur à retirer
        public void decreaseMineral(int value) {
            decreaseAmount(value);
        }

        // définit la quantité de minerai
        // value : nouvelle quantité
        public void setMineralAmount(int value) {
            setAmount(value);
        }

        // retourne la liste des cellules occupées par la pierre
        public List<GameElement> getOccupiedCells() {
            return occupiedCells;
        }

        // Supprime les pierres épuisées
        // stones : liste de toutes les pierres
        // allElements : liste de tous les éléments de la grille
        // retourne la liste des pierres supprimées
        public static List<Stone> removeDepletedStones(List<Stone> stones, List<GameElement> allElements) {
            List<Stone> toRemove = new ArrayList<>();
            for (Stone stone : stones) {
                if (stone.getCurrentMineralAmount() <= 0 && !stone.isGrowing()) {
                    toRemove.add(stone);
                    allElements.remove(stone);
                    // Retirer aussi toutes les cellules occupées
                    allElements.removeAll(stone.getOccupiedCells());

                    System.out.println("Pierre épuisée supprimée à la position (" +
                            stone.getX() + ", " + stone.getY() + ")");
                }
            }
            return toRemove;
        }

        // Génère de nouvelles pierres aléatoirement
        // stones : liste actuelle des pierres
        // allElements : tous les éléments de la grille
        // gridCols, gridRows : taille de la grille
        // ratio : proportion de cases à remplir
        public static  void generateStones(List<Stone> stones, List<GameElement> allElements, int gridCols, int gridRows, double ratio) {
            int numberToGenerate = computeNumberToGenerate(gridCols, gridRows, ratio);

            while (stones.size() < numberToGenerate) {
                // trouve une cellule libre
                GameElement cell = GameElement.getRandomFreeCell(gridCols - ADJACENT_CELL_OFFSET, gridRows - ADJACENT_CELL_OFFSET, allElements);

                // vérifie que toutes les cellules occupées par la pierre sont libres
                if (!cell.equals(GameElement.NO_POSITION) &&
                        !GameElement.isOccupied(cell.getX() + ADJACENT_CELL_OFFSET, cell.getY(), allElements) &&
                        !GameElement.isOccupied(cell.getX(), cell.getY() + ADJACENT_CELL_OFFSET, allElements) &&
                        !GameElement.isOccupied(cell.getX() + ADJACENT_CELL_OFFSET, cell.getY() + ADJACENT_CELL_OFFSET, allElements)) {

                    Stone stone = new Stone(cell);
                    stones.add(stone);                 // ajoute à la liste des pierres
                    allElements.add(stone);            // ajoute à tous les éléments
                    allElements.addAll(stone.getOccupiedCells()); // marque cellules occupées
                }
            }
        }

        // retourne le nom de la ressource
        @Override
        protected String getResourceName() {
            return "Pierre";
        }
    }