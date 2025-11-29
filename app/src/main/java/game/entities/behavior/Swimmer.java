package game.entities.behavior;

import game.maps.GameMap;
import game.tiles.Tile;
import game.tiles.behaviors.Swimmable;

public interface Swimmer {
    default double computeSwimOffsetY(GameMap map, double x, double y, int tileSize) {
        int tileX = (int)((x + tileSize / 2) / tileSize);
        int tileY = (int)((y + tileSize) / tileSize);

        Tile tile = map.getTile(tileX, tileY);
        if (tile instanceof Swimmable s)
            return s.getSubmergeOffset(tileSize);

        return 0;
    }
}
