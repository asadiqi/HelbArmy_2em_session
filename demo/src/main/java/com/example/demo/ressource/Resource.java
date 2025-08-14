package com.example.demo.ressource;

import com.example.demo.GameElement;

import java.util.List;

public abstract class Resource extends GameElement {

    protected final int DEFAULT_GROW_VALUE = 20; // valeur de croissance par défaut
    protected final int INIT_INDEX = 0;        // valeur minimale

    protected int amount;           // quantité actuelle
    protected int maxAmount;        // quantité maximale
    protected boolean isGrowing = false; // indique si la ressource est en croissance

    // Constructeur de la classe Ressource
    public Resource(int x, int y, int defaultAmount, int maxAmount) {
        super(x, y);
        this.amount = defaultAmount;
        this.maxAmount = maxAmount;
    }

    // nom de la ressource (ex: "Wood", "Stone")
    protected abstract String getResourceName();

    // Calcule le nombre de ressources à générer selon ratio
    // gridCols, gridRows : dimensions de la grille
    // ratio : proportion de cases à remplir
    // retourne le nombre de ressources à générer
    protected static int computeNumberToGenerate (int gridcols, int gridrows, double ratio) {
        return (int) (gridrows * gridcols * ratio);

    }

    // définit la quantité (entre 0 et maxAmount)
    // amount : nouvelle quantité
    public void setAmount(int amount) {
        this.amount = Math.max(INIT_INDEX, Math.min(maxAmount, amount));
    }

    // diminue la quantité d'une valeur donnée
    // value : valeur à retirer
    public void decreaseAmount(int value) {
        amount = Math.max(INIT_INDEX, amount - value);
    }

    // indique si la ressource est épuisée
    // retourne true si amount <= 0
    public boolean isDepleted() {
        return amount <= INIT_INDEX;
    }

    // indique si la ressource a atteint sa quantité max
    // retourne true si amount >= maxAmount
    public boolean isMature() {
        return amount >= maxAmount;
    }


    // Augmente la quantité d'une valeur donnée
    // value : valeur à ajouter
    public void grow(int value) {
        if (!isMature()) {
            amount = Math.min(maxAmount, amount + value);
        }
    }

    // Vérifie si la ressource pousse
    public boolean isGrowing() {
        return isGrowing; // retourne true si en croissance
    }

    // Définit si la ressource doit pousser
    public void setGrowing(boolean growing) {
        isGrowing = growing; // définit si la ressource est en croissance
    }

    // getImagePath() reste abstraite ici
    // image spécifique à chaque ressource
    @Override
    public abstract String getImagePath();

    // fait croître la ressource si en croissance
    // modifie amount, affiche messages si croissance ou maturité
    public void growResource() {
        if (isGrowing() && !isMature()) {
            int before = amount;
            grow(DEFAULT_GROW_VALUE );
            int after = amount;

            System.out.println(getResourceName() + " at (" + getX() + ", " + getY() + ") grew by " + (after - before));

            if (isMature()) {
                setGrowing(false);
                System.out.println(getResourceName() + " at (" + getX() + ", " + getY() + ") is mature");
            }
        }
    }

    // Supprime la ressource de la liste si épuisée
    // allElements : liste de tous les éléments de la grille
    // retourne true si la ressource a été retirée
    public boolean removeIfDepleted(List<GameElement> allElements) {
        if (isDepleted()) {
            allElements.remove(this);
            System.out.println(getResourceName() + " removed: " + getX() + "," + getY());
            return true;
        }
        return false;
    }

}
