package game.entities.decorations;

import java.util.List;

import engine.core.Game;
import game.entities.Entity;
import game.entities.behavior.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Decoration extends Entity implements Collidable {
    protected final List<Image> frames;
    protected final List<Integer> durations; // ms per frame
    protected int frameIndex = 0;
    protected double timer = 0; // ms accumulator

    // Solid area if needed (for obstacles)
    protected Rectangle2D solidArea;
    private int solidOffsetX, solidOffsetY;
    private int solidBaseWidth, solidBaseHeight;


    public Decoration(Game game, List<Image> frames, List<Integer> durations, double x, double y, double width, double height) {
        super(game, x, y, width, height);
        this.frames = frames;
        this.durations = durations;
        if (frames != null && !frames.isEmpty()) {
            this.image = frames.get(0);
        }
    }

    @Override
    public void update(double delta) {
        if (frames == null || frames.size() <= 1) return;
        timer += delta * 1000; // convert seconds → ms
        int currentDur = durations.get(frameIndex);
        if (timer >= currentDur) {
            timer = 0;
            frameIndex = (frameIndex + 1) % frames.size();
            image = frames.get(frameIndex);
        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (image != null)
            g.drawImage(image, x, y, width, height);
    }

    
    public void setSolidArea(int offsetX, int offsetY, int width, int height) {
        solidOffsetX = offsetX;
        solidOffsetY = offsetY;
        solidBaseWidth = width;
        solidBaseHeight = height;
        updateSolidArea();
    }

    public void updateSolidArea() {
        double scale = game.getTileSize() / game.getOriginalTileSize();
        solidArea = new Rectangle2D(
            (int)(x + solidOffsetX * scale),
            (int)(y + solidOffsetY * scale),
            (int)(solidBaseWidth * scale),
            (int)(solidBaseHeight * scale)
        );
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
