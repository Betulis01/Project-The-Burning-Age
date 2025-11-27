package game.entities.behavior;

import game.entities.Item;
import javafx.geometry.Rectangle2D;

public interface Pickupable {
    Rectangle2D getPickUpArea();
    void onPickUp(Pickupable other);
    boolean canPickUp();
}