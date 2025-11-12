package engine.map;

import engine.core.Game;
import game.entities.Entity;
import javafx.scene.image.Image;

public interface EntityFactory {
    Entity create(Game game, TiledMap.MapObject data, Image img, TiledMap map);
}
