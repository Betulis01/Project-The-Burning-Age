package game.entities.decorations.rocks;

import java.util.List;

import engine.core.Game;
import game.entities.decorations.Decoration;
import javafx.scene.image.Image;

public abstract class Rock extends Decoration {
    public Rock(Game game, List<Image> frames, List<Integer> durations,
                    double x, double y, double w, double h) {
        super(game, frames, durations, x, y, w, h);
    }
}
