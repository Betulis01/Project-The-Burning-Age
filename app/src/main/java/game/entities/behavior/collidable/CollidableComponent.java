package game.entities.behavior.collidable;

import game.entities.Entity;
import javafx.geometry.Rectangle2D;

public class CollidableComponent implements Collidable {
    private final Entity entity;
    private double offsetX, offsetY;
    private double width, height;

    public CollidableComponent(Entity entity, double offsetX, double offsetY, double width, double height) {
        this.entity = entity;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }

    @Override
    public Rectangle2D getSolidArea() {
        return new Rectangle2D(entity.getX() + offsetX, entity.getY() + offsetY, width, height);
    }
}
