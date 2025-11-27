package game.states.play;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import engine.core.Game;
import engine.map.EntityRegistry;
import engine.map.TiledMap;
import engine.physics.Collision;
import engine.render.Camera;
import game.Inventory;
import game.entities.Entity;
import game.entities.Item;
import game.entities.actors.Player;
import game.entities.actors.npc.Orc;
import game.entities.behavior.Collidable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Interactable;
import game.entities.behavior.Pickupable;
import game.entities.decorations.other.Bonfire;
import game.entities.decorations.rocks.RockMedium;
import game.entities.decorations.trees.TreeTall;
import game.entities.decorations.trees.TreeWide;
import game.entities.items.Stick;
import game.states.PlayState;
import game.tiles.GrassTile;
import game.tiles.SandTile;
import game.tiles.Tile;
import game.tiles.WaterTile;
import game.ui.InteractButton;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;

public class World extends PlayState implements GameMap {
    private final TiledMap map;
    private final EntityRegistry registry;
    private final Tile[][] tiles;
    private final List<Entity> entities = new ArrayList<>();
    private final List<Collidable> collidables = new ArrayList<>();
    private final List<Hittable> hittables = new ArrayList<>();
    private final List<Interactable> interactables = new ArrayList<>();
    private final List<Pickupable> pickupables = new ArrayList<>();

    private final Player player;
    private final Orc orc;

    private final Item stick;

    private final InteractButton interactButton;
    private final Camera camera;
    

    public World(Game game, TiledMap map) {
        super(game);
        this.map = map;

        // Load map
        this.tiles = new Tile[map.getHeight()][map.getWidth()];
        this.registry = game.getRegistry();
        registerEntityTypes();
        initializeTiles(map);
        initializeEntities(map);

        // Camera
        camera = new Camera(game.getCanvas().getWidth(), game.getCanvas().getHeight());

        // Player and actors
        player = new Player(game, this);
        orc = new Orc(game, this, game.getOriginalTileSize(), game.getOriginalTileSize());

        //Items
        stick = new Stick(game, game.getTileSize() * 252, game.getTileSize() * 252);
        
        // Load actors from map
        entities.add(player);
        entities.add(orc);
        entities.add(stick);

        // Load ui
        interactButton = new InteractButton(player.getX(),player.getY());

        camera.setZoom(3);
        System.out.println("World initialized with " + entities.size() + " entities.");
    }

    @Override
    public void update(double delta) {
        //Chat
        var keys = game.getKeyboardInput();
        if (keys.consumeKey(KeyCode.ENTER)) chat.toggle();
        if (chat.isActive()) {
            chat.update(keys);
        } else {
            player.handleInput();
        }

        //Interfaces
        collidables.clear();
        hittables.clear();
        pickupables.clear();
        interactables.clear();
        for (Entity e : entities) {
            e.update(delta);
            if (e instanceof Collidable c) collidables.add(c);
            if (e instanceof Hittable h) hittables.add(h);
            if (e instanceof Interactable i) interactables.add(i);
            if (e instanceof Pickupable p) pickupables.add(p);
        }
        Collision.handleSolidCollisions(collidables);
        Collision.handleHitCollisions(hittables);
        Collision.handlePickUpCollisions(pickupables);
        Collision.handleInteractions(interactables, player);
     

        //Camera update
        camera.update(player.getX(),player.getY(),game.getTileSize(),map.getWidth(),(map.getHeight()));
        
    }

    @Override
    public void render(GraphicsContext g) {
        g.save();
        camera.apply(g);

        int tileSize = (int)game.getTileSize();

        // compute visible tile range
        int startX = (int)(camera.getX() / tileSize);
        int endX   = (int)((camera.getX() + camera.getViewportWidth() / camera.getZoom()) / tileSize) + 1;
        int startY = (int)(camera.getY() / tileSize);
        int endY   = (int)((camera.getY() + camera.getViewportHeight() / camera.getZoom()) / tileSize) + 1;

        // clamp to map size
        startX = Math.max(0, startX);
        startY = Math.max(0, startY);
        endX   = Math.min(map.getWidth(),  endX);
        endY   = Math.min(map.getHeight(), endY);

        // --- draw only visible tiles ---
        for (int y = startY; y < endY; y++) {
            for (int x = startX; x < endX; x++) {
                Tile tile = getTile(x, y);
                if (tile == null) continue;

                double worldX = x * tileSize;
                double worldY = y * tileSize;

                tile.render(g, worldX, worldY, tileSize);
            }
        }

        // --- draw only visible entities ---
        entities.sort(Comparator.comparingDouble(Entity::getBottomY));
        for (Entity e : entities) {
            // skip off-screen entities
            double ex = e.getX();
            double ey = e.getY();
            double ew = e.getWidth();
            double eh = e.getHeight();

            boolean visible =
                ex + ew > camera.getX() &&
                ex < camera.getX() + camera.getViewportWidth() / camera.getZoom() &&
                ey + eh > camera.getY() &&
                ey < camera.getY() + camera.getViewportHeight() / camera.getZoom();

            if (visible) e.render(g);
        }

        // Interaction
        for (Interactable i : interactables) {
            if (i == player) continue;
            if (!i.canInteract()) continue;

            boolean overlapping = i.getInteractArea().intersects(player.getInteractArea());

            if (overlapping) {
                if (game.getKeyboardInput().consumeKey(KeyCode.E)) {
                    player.setInteraction(true);
                    if (i instanceof Orc o) o.setInteraction(true);
                }

                if (!player.isInteraction()) {
                    interactButton.setPosition(player.getX() + interactButton.getWidth()/2, player.getY() - 20);
                    interactButton.render(g);
                }
            } else {
                player.setInteraction(false);
                if (i instanceof Orc o) o.setInteraction(false);
            }
        }

        // PickUp
        Iterator<Pickupable> it = pickupables.iterator();
        while (it.hasNext()) {
            Pickupable p = it.next();
            if (!p.canPickUp()) continue;

            boolean overlapping = p.getPickUpArea().intersects(player.getInteractArea());

            if (overlapping && game.getKeyboardInput().consumeKey(KeyCode.E)) {
                player.getInventory().addItem((Item)p);
                it.remove(); // safely remove from pickupable list
                entities.remove(p); // remove from world entity list
                continue;
            }

            if (overlapping) {
                interactButton.setPosition(player.getX() + interactButton.getWidth() / 2,
                                        player.getY() - 20);
                interactButton.render(g);
            } else {
                player.setInteraction(false);
            }
        }


        // Chat
        chat.render(g, player);
        
        //Debug
        debug(g);
       
        g.restore();
    }

    private void initializeTiles(TiledMap map) {
        int[][] ids = map.getTileIds();
        Map<Integer, Image> images = map.getTileImages();

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                int gid = ids[y][x];
                Image img = images.get(gid);
                tiles[y][x] = createTile(gid, img);
            }
        }
    }

    private Tile createTile(int gid, Image img) {
        if (gid <= 0 || img == null) return null;
        if (gid >= 1 && gid <= 36) return new WaterTile(img);
        if (gid >= 37 && gid <= 40) return new GrassTile(img);
        if (gid >= 41 && gid <= 57) return new SandTile(img);
        return null;
    }

    private void initializeEntities(TiledMap map) {
        for (TiledMap.MapObject mo : map.getObjects()) {
            Image img = map.getTileImages().get(mo.gid);
            Entity e = createEntity(mo, img, map);
            if (e != null) entities.add(e);
        }
    }

    private void registerEntityTypes() {
        registry.register("treeWide", (g, mo, img, m) -> {
            var frames = m.getAnimatedTiles().getOrDefault(mo.gid, List.of(img));
            var durs   = m.getAnimatedDurations().getOrDefault(mo.gid, List.of(Integer.MAX_VALUE));
            double s = g.getTileSize() / m.getTileWidth();
            return new TreeWide(g, frames, durs, mo.x * s, (mo.y - mo.h) * s, mo.w * s, mo.h * s);
        });

        registry.register("treeTall", (g, mo, img, m) -> {
            var frames = m.getAnimatedTiles().getOrDefault(mo.gid, List.of(img));
            var durs   = m.getAnimatedDurations().getOrDefault(mo.gid, List.of(Integer.MAX_VALUE));
            double s = g.getTileSize() / m.getTileWidth();
            return new TreeTall(g, frames, durs, mo.x * s, (mo.y - mo.h) * s, mo.w * s, mo.h * s);
        });

        registry.register("rockMedium", (g, mo, img, m) -> {
            var frames = m.getAnimatedTiles().getOrDefault(mo.gid, List.of(img));
            var durs   = m.getAnimatedDurations().getOrDefault(mo.gid, List.of(Integer.MAX_VALUE));
            double s = g.getTileSize() / m.getTileWidth();
            return new RockMedium(g, frames, durs, mo.x * s, (mo.y - mo.h) * s, mo.w * s, mo.h * s);
        });

        registry.register("bonfire", (g, mo, img, m) -> {
            var frames = m.getAnimatedTiles().getOrDefault(mo.gid, List.of(img));
            var durs   = m.getAnimatedDurations().getOrDefault(mo.gid, List.of(Integer.MAX_VALUE));
            double s = g.getTileSize() / m.getTileWidth();
            return new Bonfire(g, frames, durs, mo.x * s, (mo.y - mo.h) * s, mo.w * s, mo.h * s);
        });
    }

    private Entity createEntity(TiledMap.MapObject mo, Image img, TiledMap map) {
        return registry.create(game, mo, img, map);
    }



    public void debug(GraphicsContext g) {
        // --- debug solid areas ---
        if (game.getKeyboardInput().isKeyPressed(javafx.scene.input.KeyCode.SPACE)) {
            for (Entity e : entities) {
                g.setLineWidth(0.4);
                if (e instanceof Collidable c && c.getSolidArea() != null) {
                    var sa = c.getSolidArea();
                    g.setStroke(javafx.scene.paint.Color.GREEN);
                    g.strokeRect(sa.getMinX(), sa.getMinY(), sa.getWidth(), sa.getHeight());
                }
                if(e instanceof Hittable h && h.getHitbox() != null) {
                    var ha = h.getHitbox();
                    g.setStroke(javafx.scene.paint.Color.RED);
                    g.strokeRect(ha.getMinX(), ha.getMinY(), ha.getWidth(), ha.getHeight());
                }
                if(e instanceof Pickupable p && p.getPickUpArea() != null) {
                    var pu = p.getPickUpArea();
                    g.setStroke(javafx.scene.paint.Color.YELLOW);
                    g.strokeRect(pu.getMinX(), pu.getMinY(), pu.getWidth(), pu.getHeight());
                }
                if(e instanceof Interactable i && i.getInteractArea() != null) {
                    var ia = i.getInteractArea();
                    g.setStroke(javafx.scene.paint.Color.BLUE);
                    g.strokeRect(ia.getMinX(), ia.getMinY(), ia.getWidth(), ia.getHeight());
                }
            }
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    //GETTERS
    public Player getPlayer() {
        return player;
    }

    public Camera getCamera() {
        return camera;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public Tile getTile(int x, int y) { return tiles[y][x]; }

    @Override
    public double getTileSize() { return game.getTileSize(); }
    @Override 
    public int getWidth() { return map.getWidth(); }

    @Override 
    public int getHeight() { return map.getHeight(); }

}
