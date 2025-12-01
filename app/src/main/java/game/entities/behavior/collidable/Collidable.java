package game.entities.behavior.collidable;

import javafx.geometry.Rectangle2D;

public interface Collidable {
    Rectangle2D getSolidArea();

    default boolean isSolid() {
        return true;
    };
    
    default void onCollide(Collidable other) {}
}
