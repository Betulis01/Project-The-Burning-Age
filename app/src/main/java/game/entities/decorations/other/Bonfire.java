package game.entities.decorations.other;

import java.util.List;

import engine.core.Game;
import game.entities.Decoration;
import javafx.scene.image.Image;

public class Bonfire extends Decoration {

    public Bonfire(Game game, List<Image> frames, List<Integer> durations,
                    double x, double y, double w, double h) {
        super(game, frames, durations, x, y, w, h);
        
        setSolidArea(
            (int) (w/game.getDeviceScale()* 0.35),
            (int) (h/game.getDeviceScale()* 0.60),
            (int) (w/game.getDeviceScale()* 0.25),
            (int) (h/game.getDeviceScale()* 0.04)
        );
    }

    @Override
    public boolean isSolid() {
        return false;
    }
}
