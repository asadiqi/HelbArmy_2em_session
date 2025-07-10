package com.example.demo.units;

import com.example.demo.GameElement;

public class Seeder extends Unit{

    public static String northSeederPath = "img/white/northSeeder.png";
    public static String southSeederPath = "img/black/southSeeder.png";
    public boolean isNorthSeeder;

    public Seeder(GameElement position) {
        super(position);
    }


    @Override
    public String getImagePath() {
        return isNorthSeeder ? northSeederPath : southSeederPath;
    }
}
