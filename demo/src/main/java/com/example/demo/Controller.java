package com.example.demo;

public class Controller {

    private View view;
    private int gridRows = 15;
    private int gridCols = 15;

    public Controller(View view) {
        this.view = view;
        view.initView(this);
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }

    public String getNorthCityFilePath() {
        return City.getNorthCityFilePath();
    }

    public String getSouthCityFilePath() {
        return City.getSouthCityFilePath();
    }

}