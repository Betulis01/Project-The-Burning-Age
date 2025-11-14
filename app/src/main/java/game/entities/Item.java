package game.entities;

import engine.core.Game;
import game.entities.behavior.Collidable;
import game.entities.behavior.Pickupable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Item extends Entity implements Collidable, Pickupable {
    protected int frameIndex = 0;
    protected double timer = 0; // ms accumulator

    // pickUp area if needed (for obstacles)
    protected Rectangle2D pickUpArea;
    private double pickUpOffsetX, pickUpOffsetY;
    private double pickUpBaseWidth, pickUpBaseHeight;

    // Solid area if needed (for obstacles)
    protected Rectangle2D solidArea;
    private double solidOffsetX, solidOffsetY;
    private double solidBaseWidth, solidBaseHeight;


    public Item(Game game, double x, double y, double width, double height) {
        super(game, x, y, width, height);
    }

    @Override
    public void update(double delta) {
        updatePickUpArea();
        updateSolidArea();

    }

    @Override
    public void render(GraphicsContext g) {
        if (image != null)
            g.drawImage(image, x, y, width, height);
    }

    
    public void setPickUpArea(int offsetX, int offsetY, int width, int height) {
        pickUpOffsetX = offsetX;
        pickUpOffsetY = offsetY;
        pickUpBaseWidth = width;
        pickUpBaseHeight = height;
        updatePickUpArea();
    }

    public void updatePickUpArea() {
        pickUpArea = new Rectangle2D(x + pickUpOffsetX * game.getScale(), y + pickUpOffsetY * game.getScale(), pickUpBaseWidth * game.getScale(),pickUpBaseHeight * game.getScale());
    }


    @Override
    public Rectangle2D getPickUpArea() {
        return pickUpArea;
    }

    public void setSolidArea(double offsetX, double offsetY, double width, double height) {
        solidOffsetX = offsetX;
        solidOffsetY = offsetY;
        solidBaseWidth = width;
        solidBaseHeight = height;
        updateSolidArea();
    }

    public void updateSolidArea() {
        solidArea = new Rectangle2D(x + solidOffsetX * game.getScale(),y + solidOffsetY * game.getScale(),solidBaseWidth * game.getScale(),solidBaseHeight * game.getScale());
    }

    @Override
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