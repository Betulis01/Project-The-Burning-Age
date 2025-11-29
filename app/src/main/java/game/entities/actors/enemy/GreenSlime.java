package game.entities.actors.enemy;

import engine.core.Game;
import game.entities.behavior.Hittable;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;

public class GreenSlime extends Enemy {
    private final Image spriteSheet;
    private Image[] images;

    public GreenSlime(Game game, double x, double y, double width, double height, double speed) {
        super(game, x, y, width, height, speed);
        this.spriteSheet = new Image(getClass().getResource("/assets/actors/enemy/greenslime.png").toExternalForm());
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    @Override
    public void move(double delta) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'move'");
    }

    @Override
    public Rectangle2D getHitbox() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHitbox'");
    }

    @Override
    public void onHit(Hittable other) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onHit'");
    }
    
}
