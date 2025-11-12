package engine.map;

import javafx.scene.image.Image;
import java.util.List;
import java.util.Map;

/**
 * Passive container for parsed map data.
 * Holds no logic for tile or entity creation.
 */
public class TiledMap {
    private final int width;
    private final int height;
    private final int tileWidth;
    private final int tileHeight;
    private final int[][] tileIds;
    private final List<MapObject> objects;
    private final Map<Integer, Image> tileImages;
    private final Map<Integer, List<Image>> animatedTiles;
    private final Map<Integer, List<Integer>> animatedDurations;

    public TiledMap(TiledMapLoader.LoadedMapData data) {
        this.width = data.width;
        this.height = data.height;
        this.tileWidth = data.tileWidth;
        this.tileHeight = data.tileHeight;
        this.tileIds = data.tileIds;
        this.objects = data.objects;
        this.tileImages = data.tileImages;
        this.animatedTiles = data.animatedTiles;
        this.animatedDurations = data.animatedDurations;
    }

    public int[][] getTileIds() { return tileIds; }
    public List<MapObject> getObjects() { return objects; }
    public Map<Integer, Image> getTileImages() { return tileImages; }
    public Map<Integer, List<Image>> getAnimatedTiles() { return animatedTiles; }
    public Map<Integer, List<Integer>> getAnimatedDurations() { return animatedDurations; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getTileWidth() { return tileWidth; }
    public int getTileHeight() { return tileHeight; }

    public static class MapObject {
        public final int gid;
        public final double x, y, w, h;
        public MapObject(int gid, double x, double y, double w, double h) {
            this.gid = gid; this.x = x; this.y = y; this.w = w; this.h = h;
        }
    }
}
