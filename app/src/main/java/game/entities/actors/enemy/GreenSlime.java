package game.entities.actors.enemy;

import engine.core.Game;
import game.entities.behavior.collidable.Collidable;
import game.entities.behavior.collidable.CollidableComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class GreenSlime extends Enemy implements Collidable {
    private final Image spriteSheet;
    private Image[] images;

    //Collision
    private final CollidableComponent collision;

    public GreenSlime(Game game, double x, double y, double width, double height, double speed) {
        super(game);
        this.spriteSheet = new Image(getClass().getResource("/assets/actors/enemy/greenslime.png").toExternalForm());
        this.collision = new CollidableComponent(this,0, 0, width, height);
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
