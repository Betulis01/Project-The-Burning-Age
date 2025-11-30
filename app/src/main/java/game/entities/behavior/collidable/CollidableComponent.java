package game.entities.behavior.collidable;

import javafx.geometry.Rectangle2D;

public class CollidableComponent {
    private Rectangle2D solidArea;
    private double offsetX, offsetY, baseWidth, baseHeight;

    public void configure(double offsetX, double offsetY, double width, double height) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.baseWidth = width;
        this.baseHeight = height;
    }

    public void update(double x, double y, double scale) {
        solidArea = new Rectangle2D(
            x + offsetX * scale,
            y + offsetY * scale,
            baseWidth * scale,
            baseHeight * scale
        );
    }

    public Rectangle2D getSolidArea() {
        return solidArea;
    }

    public boolean isSolid() {
        return true;
    }
}
