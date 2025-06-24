package com.example.demo;

public class Controller {
    private View view;

    public void setView(View view) {
        this.view = view;
    }

    public void onButtonClick() {
        view.updateLabel("Bonjour !");
    }
}
