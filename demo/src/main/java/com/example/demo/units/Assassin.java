package com.example.demo.units;

import com.example.demo.City;
import com.example.demo.GameElement;

public class Assassin extends Unit {


    public static String northAssassinPath = "img/white/northAssassin.png";
    public static String southAssassinPath = "img/black/southAssassin.png";

    public City city; // dans Collecter

    public Assassin(GameElement position, City city) {
        super(position);
        this.city = city;
    }

    @Override
    public String getImagePath() {
        return city.isNorth ? northAssassinPath : southAssassinPath;
    }


    public boolean canAttack(Unit other) {
        if (other instanceof Assassin enemy && enemy.city.isNorth != this.city.isNorth) {
            return this.hasValidTarget() && this.hasReachedTarget();
        }
        return false;
    }

}


