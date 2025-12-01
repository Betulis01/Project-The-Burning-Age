package game.entities.items;

import engine.core.Game;
import game.entities.Entity;
import game.entities.behavior.Pickupable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Item extends Entity implements Pickupable {
    protected int maxStack;
    protected Image image;
    
    //Frame
    protected int frameIndex = 0;
    protected double timer = 0; // ms accumulator

    // pickUp area if needed (for obstacles)
    protected Rectangle2D pickUpArea;
    private double pickUpOffsetX, pickUpOffsetY;
    private double pickUpBaseWidth, pickUpBaseHeight;


    public Item(Game game, double x, double y, double width, double height) {
        super(game);
    }

    @Override
    public void update(double delta) {
        updatePickUpArea();
    }

    @Override
    public void render(GraphicsContext g) {
        if (image != null)
            g.drawImage(image, x, y, width, height);
    }

    public void setPickUpArea(double offsetX, double offsetY, double width, double height) {
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

    public int getMaxStack() {
        return maxStack;
    }

    public Image getImage() {
        return image;
    }
}