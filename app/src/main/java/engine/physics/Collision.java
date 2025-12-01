package engine.physics;

import java.util.List;

import game.entities.Entity;
import game.entities.behavior.Hittable;
import game.entities.behavior.Interactable;
import game.entities.behavior.Pickupable;
import game.entities.behavior.Swimmer;
import game.entities.behavior.collidable.Collidable;
import game.maps.GameMap;
import game.tiles.Tile;
import game.tiles.behaviors.Swimmable;
import javafx.geometry.Rectangle2D;

public class Collision {

    // --- Solid collisions ---
    public static void handleSolidCollisions(List<? extends Collidable> objects) {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                var a = objects.get(i);
                var b = objects.get(j);
                if (a.getSolidArea().intersects(b.getSolidArea())) {
                    a.onCollide(b);
                    b.onCollide(a);
                }
            }
        }
    }

    // --- Hit collisions (combat) ---
    public static void handleHitCollisions(List<? extends Hittable> objects) {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                var a = objects.get(i);
                var b = objects.get(j);
                if (a.getHitbox().intersects(b.getHitbox())) {
                    // damage or combat logic here
                    a.onHit(b);
                    b.onHit(a);
                }
            }
        }
    }

        // --- Pickup collisions  ---
    public static void handlePickUpCollisions(List<? extends Pickupable> objects) {
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                var a = objects.get(i);
                var b = objects.get(j);
                if (a.getPickUpArea().intersects(b.getPickUpArea())) {
                    a.onPickUp(b);
                    b.onPickUp(a);
                }
            }
        }
    }

    // --- Interaction collisions ---
    public static void handleInteractions(List<? extends Interactable> interactables, Entity other) {
        for (Interactable i : interactables) {
            if (!i.canInteract()) continue;
            // if (i.getInteractArea().intersects(other.getInteractArea())) {
            //     i.onInteract(other);

            // }
        }
    }

    public boolean willCollideWithSolid(GameMap map, Collidable mover, double dx, double dy, int tileSize, List<Entity> entities) {
        Rectangle2D next = new Rectangle2D(mover.getSolidArea().getMinX() + dx, mover.getSolidArea().getMinY() + dy, mover.getSolidArea().getWidth(), mover.getSolidArea().getHeight());

        //--- Tile collision ---
        int left = (int)(next.getMinX() / tileSize);
        int right = ((int)(next.getMaxX() - 0.001) / tileSize);
        int top = (int)(next.getMinY() / tileSize);
        int bottom = ((int)(next.getMaxY() - 0.001) / tileSize);

        for (int y = top; y <= bottom; y++) {
            for (int x = left; x <= right; x++) {
                Tile tile = map.getTile(x, y);
                if (tile == null) continue;
                if (mover instanceof Swimmer && tile instanceof Swimmable) continue;
                if (tile.isSolid()) return true;
            }
        }

        // --- Entity collision (trees etc.) ---
        for (Entity e : entities) {
            if (e == mover) continue;
            if (e instanceof Collidable c && e instanceof Swimmable && mover instanceof Swimmer s && next.intersects(c.getSolidArea())) {
                continue; // allow swimming entities through swimmable tiles
            }            
            if (e instanceof Collidable c && c.isSolid() && next.intersects(c.getSolidArea())) {
                return true;
            }      
        }
        return false;
    }
}
