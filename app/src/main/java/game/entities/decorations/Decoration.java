package game.entities.decorations;

import java.util.List;

import engine.core.Game;
import game.entities.Entity;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class Decoration extends Entity {
    protected final List<Image> frames;
    protected final List<Integer> durations; // ms per frame
    protected int frameIndex = 0;
    protected double timer = 0; // ms accumulator


    public Decoration(Game game, List<Image> frames, List<Integer> durations, double x, double y, double width, double height) {
        super(game);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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

}
