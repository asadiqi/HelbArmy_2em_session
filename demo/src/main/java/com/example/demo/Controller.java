package com.example.demo;

public class Controller {
    private View view;
    private int gridRows = 20;
    private int gridCols = 20;

    public void setView(View view) {
        this.view = view;
    }

    public int getGridRows() {
        return gridRows;
    }

    public int getGridCols() {
        return gridCols;
    }



}