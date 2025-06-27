package com.example.demo;

public class Controller {

    private View view;
    private int gridRows = 47;
    private int gridCols = 47;

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

    public String[] getCityFilePaths() {
        return new String[] {
            City.getNorthCityFilePath(),
            City.getSouthCityFilePath()

        };
    }


}