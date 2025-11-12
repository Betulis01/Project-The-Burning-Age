package game.states.play;

import java.util.List;

import engine.render.Camera;
import game.entities.Entity;
import game.tiles.Tile;

public interface GameMap {
    Tile getTile(int x, int y);
    List<Entity> getEntities();
    Camera getCamera();
    double getTileSize();
    int getWidth();
    int getHeight();
}
