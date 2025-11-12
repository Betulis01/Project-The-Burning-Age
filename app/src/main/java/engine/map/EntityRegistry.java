package engine.map;

import java.util.HashMap;
import java.util.Map;
import engine.core.Game;
import game.entities.Entity;
import javafx.scene.image.Image;

public class EntityRegistry {
    private final Map<String, EntityFactory> factories = new HashMap<>();

    public void register(String type, EntityFactory factory) {
        factories.put(type, factory);
    }

    public Entity create(Game game, TiledMap.MapObject data, Image img, TiledMap map) {
        EntityFactory factory = factories.get(data.type); // data.type comes from Tiled
        if (factory == null) return null;
        return factory.create(game, data, img, map);
    }
}
