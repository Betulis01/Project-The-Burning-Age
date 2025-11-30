package game.entities.actors;

import engine.core.Game;
import game.entities.Entity;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Actor extends Entity {
    protected double speed;
    protected double dx, dy;

    //Stats
    protected int health;

    // Solid area
    protected Rectangle2D solidArea;
    protected double solidOffsetX, solidOffsetY;
    protected double solidBaseWidth, solidBaseHeight;

    // Solid area
    protected Rectangle2D hitbox;
    protected double hitOffsetX, hitOffsetY;
    protected double hitBaseWidth, hitBaseHeight;

    //Collision
    protected boolean collisionUp, collisionDown, collisionLeft, collisionRight;


    public Actor(Game game) {
        super(game);
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        
    }

    public void setSolidArea(double offsetX, double offsetY, double width, double height) {
        solidOffsetX = offsetX;
        solidOffsetY = offsetY;
        solidBaseWidth = width;
        solidBaseHeight = height;
        updateSolidArea();
    }

    public void updateSolidArea() {
        double scale = game.getTileSize() / game.getOriginalTileSize();
        solidArea = new Rectangle2D(
            x + solidOffsetX * scale,
            y + solidOffsetY * scale,
            solidBaseWidth * scale,
            solidBaseHeight * scale
        );
    }

    public void setHitbox(double hitWidth, double hitHeight) {
        hitBaseWidth  = (int) (width * hitWidth);
        hitBaseHeight = (int) (height * hitHeight);
        hitOffsetX = (width - hitBaseWidth) / 2;
        hitOffsetY = (height - hitBaseHeight) / 2;
        updateHitbox();
    }

    public void updateHitbox() {
        double scale = game.getTileSize() / game.getOriginalTileSize();
        hitbox = new Rectangle2D(
            x + hitOffsetX * scale,
            y + hitOffsetY * scale,
            hitBaseWidth * scale,
            hitBaseHeight * scale
        );
    }


    public Rectangle2D getSolidArea() {
        return solidArea;
    }

    // For depth sorting: the Y-coordinate of the “feet” or base
    @Override
    public double getBottomY() {
        if (solidArea != null) {
            return solidArea.getMinY() + solidArea.getHeight(); // bottom in world coords
        }
        return y; // fallback
    } 
}
