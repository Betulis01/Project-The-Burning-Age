package game.entities;

import engine.core.Game;
import game.entities.behavior.Renderable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Entity implements Renderable {
    protected Game game;
    protected Image image;
    protected double x, y;
    protected double width, height;

    public Entity (Game game) {
        this.game = game;
    }

    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        
    }


    public void setPosition(double x, double y) {this.x = x;this.y = y;}
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

}
