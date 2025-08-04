package com.example.demo.ressource;

import com.example.demo.GameElement;

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
}
