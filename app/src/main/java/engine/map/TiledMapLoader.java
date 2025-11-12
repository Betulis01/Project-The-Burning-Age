package engine.map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

/**
 * Loads a Tiled (.json + .tsx) map into raw structured data.
 * Does no entity or tile instantiation. Only parsing and cropping.
 */
public class TiledMapLoader {

    public LoadedMapData load(String resourcePath) {
        try (InputStreamReader reader =
                     new InputStreamReader(reqStream(resourcePath))) {

            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

            int width = root.get("width").getAsInt();
            int height = root.get("height").getAsInt();
            int tileWidth = root.get("tilewidth").getAsInt();
            int tileHeight = root.get("tileheight").getAsInt();

            // -------- layers --------
            JsonArray layers = root.getAsJsonArray("layers");

            // 1. tile layer. makes a int[][] of tile ids
            JsonObject tileLayer = null;
            for (JsonElement le : layers) {
                JsonObject lo = le.getAsJsonObject();
                if ("tilelayer".equals(lo.get("type").getAsString())) {
                    tileLayer = lo;
                    break;
                }
            }
            if (tileLayer == null) throw new IOException("No tilelayer in map");

            JsonArray data = tileLayer.getAsJsonArray("data");
            int[][] tileIds = new int[height][width];
            for (int i = 0; i < data.size(); i++) {
                tileIds[i / width][i % width] = data.get(i).getAsInt();
            }

            // 2. object layers
            List<TiledMap.MapObject> objects = new ArrayList<>();
            for (JsonElement le : layers) {
                JsonObject lo = le.getAsJsonObject();
                if ("objectgroup".equals(lo.get("type").getAsString())) {
                    JsonArray objs = lo.getAsJsonArray("objects");
                    for (JsonElement oe : objs) {
                        JsonObject oo = oe.getAsJsonObject();
                        if (!oo.has("gid")) continue;

                        int gid = oo.get("gid").getAsInt();
                        double x = oo.get("x").getAsDouble();
                        double y = oo.get("y").getAsDouble();
                        double w = oo.get("width").getAsDouble();
                        double h = oo.get("height").getAsDouble();
                        String type = oo.has("type") ? oo.get("type").getAsString() : "";
                        objects.add(new TiledMap.MapObject(gid, x, y, w, h, type));

                    }
                }
            }

            // -------- tilesets --------
            List<Tileset> tilesets = new ArrayList<>();
            JsonArray tsArr = root.getAsJsonArray("tilesets");
            for (JsonElement e : tsArr) {
                JsonObject tsObj = e.getAsJsonObject();
                int firstGid = tsObj.get("firstgid").getAsInt();
                String tsxRel = tsObj.get("source").getAsString();

                String tsxPath = resolveRelative(resourcePath, tsxRel);
                Tileset ts = loadTileset(tsxPath, firstGid);
                tilesets.add(ts);
            }
            tilesets.sort(Comparator.comparingInt(t -> t.firstGid));
            
            Map<Integer, Image> tileImages = new HashMap<>();
            Map<Integer, List<Image>> animatedTiles = new HashMap<>();
            Map<Integer, List<Integer>> animatedDurations = new HashMap<>();
        
            // Build gid → image cache with correct per-tileset size
            for (Tileset ts : tilesets) {
                int totalTiles = (int) (ts.image.getWidth() / ts.tileWidth)
                            * (int) (ts.image.getHeight() / ts.tileHeight);
                for (int localId = 0; localId < totalTiles; localId++) {
                    int gid = ts.firstGid + localId;
                    int cols = ts.columns;
                    int srcX = (localId % cols) * ts.tileWidth;
                    int srcY = (localId / cols) * ts.tileHeight;

                    Image img = new WritableImage(
                            ts.image.getPixelReader(),
                            srcX, srcY,
                            ts.tileWidth, ts.tileHeight
                    );
                    tileImages.put(gid, img);
                }

                // add animations
                for (var entry : ts.animations.entrySet()) {
                    int baseGid = ts.firstGid + entry.getKey();
                    List<Image> frames = new ArrayList<>();
                    List<Integer> durs = ts.frameDurations.get(entry.getKey());
                    for (int localFrameId : entry.getValue()) {
                        int gid = ts.firstGid + localFrameId;
                        frames.add(tileImages.get(gid));
                    }
                    animatedTiles.put(baseGid, frames);
                    animatedDurations.put(baseGid, durs);
                }
            }


            return new LoadedMapData(width, height, tileWidth, tileHeight, tileIds, objects, tileImages, animatedTiles, animatedDurations);

        } catch (IOException ex) {
            throw new RuntimeException("Failed to load map: " + resourcePath, ex);
        }
    }

    // --- data holder ---
    public static class LoadedMapData {
        public final int width, height, tileWidth, tileHeight;
        public final int[][] tileIds;
        public final List<TiledMap.MapObject> objects;
        public final Map<Integer, Image> tileImages;
        public final Map<Integer, List<Image>> animatedTiles;
        public final Map<Integer, List<Integer>> animatedDurations;

        public LoadedMapData(int width, int height, int tileWidth, int tileHeight,
                            int[][] tileIds,
                            List<TiledMap.MapObject> objects,
                            Map<Integer, Image> tileImages,
                            Map<Integer, List<Image>> animatedTiles,
                            Map<Integer, List<Integer>> animatedDurations) {
            this.width = width;
            this.height = height;
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.tileIds = tileIds;
            this.objects = objects;
            this.tileImages = tileImages;
            this.animatedTiles = animatedTiles;
            this.animatedDurations = animatedDurations;
        }
    }


    // --- internal helpers ---
    private InputStream reqStream(String path) throws IOException {
        InputStream in = getClass().getResourceAsStream(path);
        if (in == null) throw new IOException("Resource not found: " + path);
        return in;
    }

    private String resolveRelative(String base, String rel) {
        int slash = base.lastIndexOf('/');
        String baseDir = (slash >= 0) ? base.substring(0, slash + 1) : "/";
        if (rel.startsWith("/")) return rel;

        String joined = baseDir + rel;
        String[] parts = joined.split("/");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p.isEmpty() || p.equals(".")) continue;
            if (p.equals("..")) {
                if (!out.isEmpty()) out.remove(out.size() - 1);
            } else out.add(p);
        }
        return "/" + String.join("/", out);
    }

    private Tileset loadTileset(String tsxPath, int firstGid) throws IOException {
        String xml;
        try (InputStream in = reqStream(tsxPath)) {
            xml = new String(in.readAllBytes());
        }

        int tw = parseIntAttr(xml, "tilewidth");
        int th = parseIntAttr(xml, "tileheight");

        String imageSource = parseStringAttrInTag(xml, "image", "source");
        int imgWidth = parseIntAttrInTag(xml, "image", "width");

        int slash = tsxPath.lastIndexOf('/');
        String tsxDir = (slash >= 0) ? tsxPath.substring(0, slash + 1) : "/";
        String imagePath = joinDir(tsxDir, imageSource);
        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        int columns = imgWidth / tw;

        Tileset ts = new Tileset();
        ts.firstGid = firstGid;
        ts.image = img;
        ts.columns = columns;
        ts.tileWidth = tw;
        ts.tileHeight = th;
        ts.animations = new HashMap<>();
        ts.frameDurations = new HashMap<>();

        // --- Parse animation blocks ---
        Pattern tilePattern = Pattern.compile("<tile id=\"(\\d+)\">([\\s\\S]*?)</tile>");
        Matcher tileMatcher = tilePattern.matcher(xml);
        while (tileMatcher.find()) {
            int tileId = Integer.parseInt(tileMatcher.group(1));
            String tileContent = tileMatcher.group(2);

            if (!tileContent.contains("<animation>"))
                continue;

            List<Integer> frameIds = new ArrayList<>();
            List<Integer> durations = new ArrayList<>();

            Matcher frameMatcher = Pattern.compile(
                    "<frame tileid=\"(\\d+)\" duration=\"(\\d+)\""
            ).matcher(tileContent);

            while (frameMatcher.find()) {
                frameIds.add(Integer.parseInt(frameMatcher.group(1)));
                durations.add(Integer.parseInt(frameMatcher.group(2)));
            }

            if (!frameIds.isEmpty()) {
                ts.animations.put(tileId, frameIds);
                ts.frameDurations.put(tileId, durations);
            }
        }

        return ts;
    }


    private String joinDir(String dir, String file) {
        if (file.startsWith("/")) return file;
        if (dir.endsWith("/")) return dir + file;
        return dir + "/" + file;
    }

    private int parseIntAttr(String xml, String attr) throws IOException {
        int idx = xml.indexOf(attr + "=\"");
        if (idx < 0) throw new IOException("Missing " + attr);
        int start = idx + attr.length() + 2;
        int end = xml.indexOf("\"", start);
        return Integer.parseInt(xml.substring(start, end));
    }

    private int parseIntAttrInTag(String xml, String tag, String attr) throws IOException {
        int tagIdx = xml.indexOf("<" + tag);
        if (tagIdx < 0) throw new IOException("Missing <" + tag + ">");
        int idx = xml.indexOf(attr + "=\"", tagIdx);
        if (idx < 0) throw new IOException("Missing " + attr);
        int start = idx + attr.length() + 2;
        int end = xml.indexOf("\"", start);
        return Integer.parseInt(xml.substring(start, end));
    }

    private String parseStringAttrInTag(String xml, String tag, String attr) throws IOException {
        int tagIdx = xml.indexOf("<" + tag);
        if (tagIdx < 0) throw new IOException("Missing <" + tag + ">");
        int idx = xml.indexOf(attr + "=\"", tagIdx);
        if (idx < 0) throw new IOException("Missing " + attr);
        int start = idx + attr.length() + 2;
        int end = xml.indexOf("\"", start);
        return xml.substring(start, end);
    }

    private static class Tileset {
        int firstGid;
        Image image;
        int columns;
        int tileWidth, tileHeight;
        Map<Integer, List<Integer>> animations = new HashMap<>();
        Map<Integer, List<Integer>> frameDurations = new HashMap<>();
    }

}
