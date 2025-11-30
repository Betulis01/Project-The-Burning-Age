package game.entities.behavior.collidable;

import javafx.geometry.Rectangle2D;

public interface Collidable {
    Rectangle2D getSolidArea();
    boolean isSolid();
    default void onCollide(Collidable other) {}
}
