package com.example.demo.units;

import com.example.demo.GameElement;

public class Collecter extends Unit {

    public static String northCollecterPath = "img/white/northCollecter.png";
    public static String southCollecterPath = "img/black/southCollecter.png";
    private boolean isNorthCollecter;
    private boolean isLumberjackCollecter;
    private boolean isPikerCollecter;


    public Collecter(GameElement position, boolean isNorthCollecter, boolean isLumberjackCollecter, boolean isPikerCollecter) {
        super(position);
        this.isNorthCollecter = isNorthCollecter;
        this.isLumberjackCollecter = isLumberjackCollecter;
        this.isPikerCollecter = isPikerCollecter;
    }


    @Override
    public String getImagePath() {
        return isNorthCollecter ? northCollecterPath : southCollecterPath;
    }

    public int getBonus(String ressourceType) {
        // ressourceType=ressourceType.toLowerCase();
        if (ressourceType.equals("tree")) {
            if (isLumberjackCollecter) {
                return 2;
            } else {
                return 1;
            }
        } else if (ressourceType.equals("stone")) {
            if (isPikerCollecter) {
                return 3;
            } else {
                return 1;
            }

        } else {
            return 1;
        }
    }


}
