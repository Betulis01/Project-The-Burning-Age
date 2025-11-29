package game.entities;

import engine.core.Game;
import game.entities.behavior.Interactable;
import game.entities.behavior.Renderable;
import game.entities.behavior.Updateable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity implements Renderable, Updateable, Interactable {
    protected Game game;
    protected Image image;
    protected double x, y;
    protected double width, height;

    // Solid area
    protected Rectangle2D interactArea;
    protected double interactOffsetX, interactOffsetY;
    protected double interactBaseWidth, interactBaseHeight;


    public Entity (Game game, double x, double y, double width, double height) {
        this.game = game;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(double delta) {
    
    }

    @Override
    public void render(GraphicsContext g) {
        
    }


    // --Interactable--
    public void setInteractArea(double interactWidth, double interactHeight) {
        interactBaseWidth  = (int) (width * interactWidth);
        interactBaseHeight = (int) (height * interactHeight);
        interactOffsetX = (width - interactBaseWidth) / 2;
        interactOffsetY = (height - interactBaseHeight);
        updateInteractArea();
    }

    public void updateInteractArea() {
        double scale = game.getTileSize() / game.getOriginalTileSize();
        interactArea = new Rectangle2D(
            x + interactOffsetX * scale,
            y + interactOffsetY * scale,
            interactBaseWidth * scale,
            interactBaseHeight * scale
        );
    }

    @Override
    public Rectangle2D getInteractArea() {
        return interactArea;
    }

    @Override
    public void onInteract(Entity other) {
        
    }

    @Override
    public boolean canInteract() {
        return false;
    }


    public void setPosition(double x, double y) {this.x = x;this.y = y;}
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }


}
