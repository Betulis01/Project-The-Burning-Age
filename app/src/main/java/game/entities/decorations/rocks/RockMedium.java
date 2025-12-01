package game.entities.decorations.rocks;

import java.util.List;

import engine.core.Game;
import game.entities.behavior.collidable.Collidable;
import game.entities.behavior.collidable.CollidableComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class RockMedium extends Rock implements Collidable {
    private final CollidableComponent collision;
    public RockMedium(Game game, List<Image> frames, List<Integer> durations,
                    double x, double y, double w, double h) {
        super(game, frames, durations, x, y, w, h);
        this.collision = new CollidableComponent(this,(width-8)/2, (height-6),7, 4);
    }

    @Override
    public Rectangle2D getSolidArea() {
        return collision.getSolidArea();
    }

    @Override
    public double getBottomY() {
        if (collision.getSolidArea() != null) {
            return collision.getSolidArea().getMaxY(); // already equals y + height
        }
        return y + height; // fallback
    }
}

