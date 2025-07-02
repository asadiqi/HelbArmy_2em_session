package com.example.demo.units;

import com.example.demo.GameElement;

public class Collecter extends Unit {

    public static String northCollecterPath = "img/white/northCollecter.png";
    public static String southCollecterPath = "img/black/southCollecter.png";
    private boolean isNorthCollecter;


    public Collecter(GameElement position, boolean isNorthCollecter) {
        super(position);
        this.isNorthCollecter = isNorthCollecter;
    }


    @Override
    public String getImagePath() {
        return isNorthCollecter ? northCollecterPath : southCollecterPath;
    }



}
