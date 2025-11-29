package game.tiles;

import game.tiles.behaviors.Walkable;
import javafx.scene.image.Image;

public class DesertTile extends Tile implements Walkable {
    public DesertTile(Image image) {
        super(image);
    }

    @Override
    public boolean canWalk() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'canWalk'");
    }
}
