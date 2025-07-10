package com.example.demo.units;

import com.example.demo.GameElement;

public class Seeder extends Unit{

    public static String northSeederPath = "img/white/northSeeder.png";
    public static String southSeederPath = "img/black/southSeeder.png";
    public boolean isNorthSeeder;
    private String targerRessourceType;

    public Seeder(GameElement position, boolean isNorthSeeder) {
        super(position);
        this.isNorthSeeder = isNorthSeeder;

    }


    @Override
    public String getImagePath() {
        return isNorthSeeder ? northSeederPath : southSeederPath;
    }

    public String getTargerRessourceType() {
        return targerRessourceType;
    }


    public void setTargerRessourceType(String type) {
        this.targerRessourceType = type;
    }


}
