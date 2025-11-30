package game.entities.actors.player;

import engine.core.Game;
import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.physics.Collision;
import engine.render.Camera;
import game.entities.Entity;
import game.entities.actors.Actor;
import game.entities.behavior.Controllable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Moveable;
import game.entities.behavior.Swimmer;
import game.entities.behavior.collidable.Collidable;
import game.maps.GameMap;
import game.tiles.Tile;
import game.ui.inventory.Inventory;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class Player extends Actor implements Hittable, Controllable, Moveable, Swimmer {
    private GameMap map;
    private final Image spriteSheet;
    private Image[][] animations;
    private int pixels = 32; 
    private Inventory inventory;


    // Animation
    private enum Direction {
        UP, UPRIGHT, RIGHT, DOWNRIGHT, DOWN, DOWNLEFT, LEFT, UPLEFT
    }
    private Direction direction = Direction.DOWN;
    private double aniTimer;
    private int aniIndex;
    private final double aniSpeed = 0.2; // seconds per frame 
    
    // Movement
    private boolean up = false, down = false, left = false, right = false;
    private boolean moving;
    
    // Interaction
    private boolean interaction = false;


    public Player(Game game, double x, double y, double w, double h, int speed) {
        super(game);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.speed = speed;
        
        spriteSheet = new Image(getClass().getResource("/assets/actors/player/orc8.png").toExternalForm());
        inventory = new Inventory(4,4);
        loadAnimations();
        setSolidArea(pixels * 0.42,pixels * 0.85,pixels * 0.15,pixels * 0.08);
        setHitbox(0.3,0.5);
        setInteractArea(1, 1);
    }

    @Override
    public void update(double delta) {
        handleInput();
        move(delta);
        updateAnimation(delta);
        setAnimationDirection();
    }

    @Override
    public void render(GraphicsContext g) {
        Image frame = animations[aniIndex][direction.ordinal()];
        int tileSize = (int) game.getTileSize();

        // Feet position tiles
        int tileX = (int)((x + width / 2) / tileSize);
        int tileY = (int)((y + height)    / tileSize);

        // Clamp to valid bounds
        tileX = Math.max(0, Math.min(tileX, map.getWidth()  - 1));
        tileY = Math.max(0, Math.min(tileY, map.getHeight() - 1));

        // Current + below tile (safe clamped)
        Tile current = map.getTile(tileX, tileY);
        int belowY = Math.min(tileY + 1, map.getHeight() - 1);
        Tile below = map.getTile(tileX, belowY);

        // Swimming offset logic
        boolean inWater  = current instanceof game.tiles.behaviors.Swimmable;
        boolean waterEnd = !(below instanceof game.tiles.behaviors.Swimmable);

        double offsetY = 0;
        if (inWater) {
            offsetY = -5;  // adjust height if inside water

            // If standing at water edge, push slightly more up for realism
            if (waterEnd) {
                offsetY = -8;
            }
        }

        // Not in water → draw normally
        if (!inWater) {
            g.drawImage(frame, x, y + offsetY, width, height);
            return;
        }

        // In water → clip lower portion
        g.save();

        double visibleHeight = height * 0.48;
        double clipY = y + offsetY;

        g.beginPath();
        g.rect(x, clipY, width, visibleHeight);
        g.closePath();
        g.clip();

        g.drawImage(frame, x, y + offsetY, width, height);

        g.restore();
    }


    @Override
    public void handleInput() {
        up = down = left = right = false;
        KeyboardInput keys = game.getKeyboardInput();
        if (keys.isKeyPressed(KeyCode.W)) {
            up = true;
        }
        if (keys.isKeyPressed(KeyCode.S)) {
            down = true;
        }
        if (keys.isKeyPressed(KeyCode.A)) {
            left = true;
        }
        if (keys.isKeyPressed(KeyCode.D)) {
            right = true;
        }
    }

    @Override
    public void move(double delta) {
        updateSolidArea();
        updateHitbox();
        updateInteractArea();
        moving = false;

        boolean horizontal = left ^ right;
        boolean vertical = up ^ down;
        double moveSpeed = (horizontal && vertical) ? (speed / Math.sqrt(2.0)) * delta : speed * delta;

        double dx = 0;
        double dy = 0;

        Collision collision = game.getCollision();
        int tileSize = (int) game.getTileSize();

        // Try each direction only if not colliding
        if (up && !down && !collisionUp) {
            if (!collision.willCollideWithSolid(map, this, 0, -moveSpeed, tileSize, map.getEntities())) {
                dy -= moveSpeed;
                moving = true;
            }
        }
        if (down && !up && !collisionDown) {
            if (!collision.willCollideWithSolid(map, this, 0, moveSpeed, tileSize, map.getEntities())) {
                dy += moveSpeed;
                moving = true;
            }
        }
        if (left && !right && !collisionLeft) {
            if (!collision.willCollideWithSolid(map, this, -moveSpeed, 0, tileSize, map.getEntities())) {
                dx -= moveSpeed;
                moving = true;
            }
        }
        if (right && !left && !collisionRight) {
            if (!collision.willCollideWithSolid(map, this, moveSpeed, 0, tileSize, map.getEntities())) {
                dx += moveSpeed;
                moving = true;
            }
        }

        // Apply if no solid collision on the combined move
        if (!collision.willCollideWithSolid(map, this, dx, dy, tileSize, map.getEntities())) {
            x += dx;
            y += dy;
        }

        // Clamp inside world bounds
        double maxX = (map.getWidth()  * game.getTileSize()) - width;
        double maxY = (map.getHeight() * game.getTileSize()) - height;
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
    }


    private void updateAnimation(double delta) {
        if (moving) {
            aniTimer += delta;
            if (aniTimer >= aniSpeed) {
                aniTimer = 0;
                aniIndex++;
                if (aniIndex > 2) aniIndex = 1; // loop between 1–2 while moving
            }
        } else {
            aniIndex = 0; // idle frame
        }
    }

    private void setAnimationDirection() {
        MouseInput mouse = game.getMouseInput();
        Camera camera = map.getCamera();

        // Convert screen → world coordinates
        double mxWorld = camera.getX() + mouse.getMouseX() / camera.getZoom();
        double myWorld = camera.getY() + mouse.getMouseY() / camera.getZoom();

        // Player center
        double px = x + width / 2;
        double py = y + height / 2;

        double dx = mxWorld - px;
        double dy = myWorld - py;

        double angle = Math.toDegrees(Math.atan2(dy, dx));
        if (angle < 0) angle += 360;

        // Face mouse when stationary and right-click pressed
        if (!moving && mouse.isButtonDown(MouseButton.SECONDARY)) {
            if (angle >= 337.5 || angle < 22.5) direction = Direction.RIGHT;
            else if (angle < 67.5)  direction = Direction.DOWNRIGHT;
            else if (angle < 112.5) direction = Direction.DOWN;
            else if (angle < 157.5) direction = Direction.DOWNLEFT;
            else if (angle < 202.5) direction = Direction.LEFT;
            else if (angle < 247.5) direction = Direction.UPLEFT;
            else if (angle < 292.5) direction = Direction.UP;
            else direction = Direction.UPRIGHT;
        } 
        // Face movement
        else {
            if (up && right)       direction = Direction.UPRIGHT;
            else if (up && left)   direction = Direction.UPLEFT;
            else if (down && right)direction = Direction.DOWNRIGHT;
            else if (down && left) direction = Direction.DOWNLEFT;
            else if (up)           direction = Direction.UP;
            else if (down)         direction = Direction.DOWN;
            else if (left)         direction = Direction.LEFT;
            else if (right)        direction = Direction.RIGHT;
        }
    }

    @Override
    public double getBottomY() {
        if (solidArea != null) {
            return solidArea.getMaxY(); // already equals y + height
        }
        return y + height; // fallback
    }


    private void loadAnimations() {
        int frameCount = 3;  // 3 frames per direction
        int directionCount = 8;
        animations = new Image[frameCount][directionCount];

        PixelReader reader = spriteSheet.getPixelReader();
        for (int dir = 0; dir < directionCount; dir++) {
            for (int frame = 0; frame < frameCount; frame++) {
                animations[frame][dir] = new WritableImage(reader,
                    frame * pixels,    // x offset (frame)
                    dir * pixels,      // y offset (direction)
                    pixels, pixels);
            }
        }
    }


    // --Hittable--
    @Override
    public Rectangle2D getHitbox() {
        return hitbox; // or a slightly larger area if you want pickup overlap
    }

    @Override
    public void onHit(Hittable other) {
        //System.out.println("Hitted!");
    }


    // --Collidable--
    @Override
    public Rectangle2D getSolidArea() {
        return solidArea;
    }

    @Override
    public boolean isSolid() {
        return true; // blocks movement for other entities
    }

    @Override
    public void onCollide(Collidable other) {
        // Example reactions:
        // if (other instanceof Enemy) takeDamage(1);
        // if (other instanceof ItemDrop) pickup((ItemDrop) other);
    }   

    // --Interactable--
    @Override
    public Rectangle2D getInteractArea() {
        return interactArea;
    }

    @Override
    public void onInteract(Entity other) {
        
    }

    @Override
    public boolean canInteract() {
        return true;
    }

    public boolean isInteraction() {
        return interaction;
    }

    public void setInteraction(boolean interaction) {
        this.interaction = interaction;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setMap(GameMap map) {
        this.map = map;
    }
    

}
