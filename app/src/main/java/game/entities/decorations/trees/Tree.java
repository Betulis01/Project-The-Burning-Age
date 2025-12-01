package game.entities.decorations.trees;

import java.util.List;

import engine.core.Game;
import game.entities.decorations.Decoration;
import javafx.scene.image.Image;

public abstract class Tree extends Decoration {
    public Tree(Game game, List<Image> frames, List<Integer> durations,
                    double x, double y, double w, double h) {
        super(game, frames, durations, x, y, w, h);
    }
}
