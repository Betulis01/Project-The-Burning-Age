package game.entities.items;

import engine.core.Game;
import game.entities.behavior.Pickupable;
import game.entities.behavior.collidable.Collidable;
import game.entities.behavior.collidable.CollidableComponent;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utilities.SpriteSheet;

public class Stick extends Item implements Collidable {
    private final CollidableComponent collision;

    public Stick(Game game, double x, double y) {
        super(game,x,y, 16, 16);

        SpriteSheet sheet = new SpriteSheet(new Image(getClass().getResource("/assets/items/Stick.png").toExternalForm()),16, 16);        
        this.image = sheet.getFrame(0, 0);
        this.maxStack = 4;
        this.collision = new CollidableComponent(this,(width-8)/2, (height-6),7, 4);

        setPickUpArea(0, 0, width, height);
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        g.drawImage(image, x, y);
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

 
    @Override
    public void onPickUp(Pickupable other) {

    }

    @Override
    public boolean canPickUp() {
        return true;
    }
}
