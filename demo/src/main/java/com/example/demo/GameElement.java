package com.example.demo;

import java.util.Objects;

public class GameElement {
    protected int x;
    protected int y;
    protected boolean isActive;

    public GameElement(int x, int y) {
        this.x = x;
        this.y = y;
        this.isActive = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean samePosition(GameElement other) {
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        GameElement other = (GameElement) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "GameElement{" + "x=" + x + ", y=" + y + ", isActive=" + isActive + '}';
    }

   // public abstract void update();
}
