package com.example.demo.collectable;
import com.example.demo.GameElement;
import com.example.demo.units.Unit;

import java.util.List;

public class Flag extends GameElement {

    public static String flagImagePath = "img/flag.png";
    public static final Flag NO_FLAG = new Flag(GameElement.NO_POSITION);

    public Flag(GameElement position) {
        super(position.getX(), position.getY());
    }

    @Override
    public String getImagePath() {
        return flagImagePath;
    }

    // Renvoie le drapeau créé ou null si déjà présent ou pas de place
    public static Flag createFlagIfNone(List<GameElement> allElements, int gridRows, int gridCols) {
        for (GameElement e : allElements) {
            if (e instanceof Flag ) return NO_FLAG; // déjà un drapeau
        }
        GameElement freePos = GameElement.getRandomFreeCell(gridCols, gridRows, allElements);

        // Vérifie si la position contient déjà un MagicStone
        for (GameElement e : allElements) {
            if (e.getX() == freePos.getX() && e.getY() == freePos.getY() && e instanceof MagicStone) {
                return NO_FLAG; // pas de drapeau sur MagicStone
            }
        }

        if (!freePos.equals(GameElement.NO_POSITION)) {
            Flag newFlag = new Flag(freePos);
            allElements.add(newFlag);
            return newFlag;
        }
        return NO_FLAG;
    }

    public boolean handleUnitArrival(Unit unit, List<GameElement> allElements) {
        if (unit.getX() == this.getX() && unit.getY() == this.getY()) {
            allElements.remove(this);
            // Reset target for all units
            for (GameElement e : allElements) {
                if (e instanceof Unit u) {
                    u.setTarget(GameElement.NO_POSITION);
                }
            }

            return true; // flag retiré
        }
        return false; // flag toujours présent
    }


}
