    package com.example.demo.ressource;

    import com.example.demo.GameElement;
    import java.util.ArrayList;
    import java.util.List;

    public class Stone extends Resource {

        public static String stonePath = "img/stone.png";
        private static final int DEFAULT_MINERAL_AMOUNT = 100;
        private static final int MAX_MINERAL_AMOUNT = 200;
        private double stoneRatio = 2.0;

        private List<GameElement> occupiedCells;

        public Stone(GameElement position) {
            super(position.getX(), position.getY(), DEFAULT_MINERAL_AMOUNT, MAX_MINERAL_AMOUNT);

            this.occupiedCells = new ArrayList<>();
            occupiedCells.add(new GameElement(x, y));
            occupiedCells.add(new GameElement(x + 1, y));
            occupiedCells.add(new GameElement(x, y + 1));
            occupiedCells.add(new GameElement(x + 1, y + 1));
        }

        @Override
        public String getImagePath() {
            return stonePath;
        }

        @Override
        public double getWidthRatio() {
            return stoneRatio;
        }

        @Override
        public double getHeightRatio() {
            return stoneRatio;
        }

        public int getCurrentMineralAmount() {
            return amount;
        }

        public void decreaseMineral(int value) {
            decreaseAmount(value);
        }

        public void setMineralAmount(int value) {
            setAmount(value);
        }

        public List<GameElement> getOccupiedCells() {
            return occupiedCells;
        }


        @Override
        protected String getResourceName() {
            return "Pierre";
        }


        public static List<Stone> removeDepletedStones(List<Stone> stones, List<GameElement> allElements) {
            List<Stone> toRemove = new ArrayList<>();
            for (Stone stone : stones) {
                if (stone.removeIfDepleted(allElements)) {
                    toRemove.add(stone);
                }
            }
            return toRemove;
        }


        public static  void generateStones(List<Stone> stones, List<GameElement> allElements, int gridCols, int gridRows, double ratio) {
            int numberToGenerate = computeNumberToGenerate(gridCols, gridRows, ratio);

            while (stones.size() < numberToGenerate) {
                GameElement cell = GameElement.getRandomFreeCell(gridCols - 1, gridRows - 1, allElements);
                if (!cell.equals(GameElement.NO_POSITION) &&
                        !GameElement.isOccupied(cell.getX() + 1, cell.getY(), allElements) &&
                        !GameElement.isOccupied(cell.getX(), cell.getY() + 1, allElements) &&
                        !GameElement.isOccupied(cell.getX() + 1, cell.getY() + 1, allElements)) {

                    Stone stone = new Stone(cell);
                    stones.add(stone);
                    allElements.add(stone);
                    allElements.addAll(stone.getOccupiedCells());
                }
            }
        }

    }
