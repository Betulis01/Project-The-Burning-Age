package game.entities.behavior;

import game.entities.Entity;
import javafx.geometry.Rectangle2D;

public interface Pickupable {
    Rectangle2D getPickUpArea();
    void onPickUp(Entity other);
    boolean canPickUp();
}