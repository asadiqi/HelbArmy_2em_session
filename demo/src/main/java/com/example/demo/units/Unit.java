package com.example.demo.units;

import com.example.demo.GameElement;

public class Unit extends GameElement {
    public Unit(GameElement position) {
        super(position.getX(),position.getY());
    }
}
