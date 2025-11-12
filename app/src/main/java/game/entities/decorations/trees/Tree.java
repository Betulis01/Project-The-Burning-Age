package game.entities.decorations.trees;

import java.util.List;

import engine.core.Game;
import game.entities.Decoration;
import javafx.scene.image.Image;

public class Tree extends Decoration {
    public Tree(Game game, List<Image> frames, List<Integer> durations,
                    double x, double y, double w, double h) {
        super(game, frames, durations, x, y, w, h);
    }

    @Override
    public boolean isSolid() {
       return true;
    }

    @Override
    public double getBottomY() {
        if (solidArea != null) {
            return solidArea.getMinY() + solidArea.getHeight();
        } else if (frames != null && !frames.isEmpty()) {
            return y + frames.get(0).getHeight();
        } else {
            return y;
        }
    }
}
