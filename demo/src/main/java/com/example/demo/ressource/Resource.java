package com.example.demo.ressource;

import com.example.demo.GameElement;

import java.util.List;

public abstract class Resource extends GameElement {

    protected int amount;
    protected int maxAmount;
    protected boolean isGrowing = false;

    public Resource(int x, int y, int defaultAmount, int maxAmount) {
        super(x, y);
        this.amount = defaultAmount;
        this.maxAmount = maxAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = Math.max(0, Math.min(maxAmount, amount));
    }

    public void decreaseAmount(int value) {
        amount = Math.max(0, amount - value);
    }

    public boolean isDepleted() {
        return amount <= 0;
    }

    public boolean isMature() {
        return amount >= maxAmount;
    }

    public void grow(int value) {
        if (!isMature()) {
            amount = Math.min(maxAmount, amount + value);
        }
    }

    public boolean isGrowing() {
        return isGrowing;
    }

    public void setGrowing(boolean growing) {
        isGrowing = growing;
    }

    // getImagePath() reste abstraite ici
    @Override
    public abstract String getImagePath();


    public void growResource() {
        if (isGrowing() && !isMature()) {
            int before = amount;
            grow(20);
            int after = amount;

            System.out.println(getResourceName() + " en (" + getX() + ", " + getY() + ") a poussé de " + (after - before));

            if (isMature()) {
                setGrowing(false);
                System.out.println(getResourceName() + " en (" + getX() + ", " + getY() + ") est mature");
            }
        }
    }

    protected abstract String getResourceName();



    public boolean removeIfDepleted(List<GameElement> allElements) {
        if (isDepleted()) {
            allElements.remove(this);
            System.out.println(getResourceName() + " retiré: " + getX() + "," + getY());
            return true;
        }
        return false;
    }






}
