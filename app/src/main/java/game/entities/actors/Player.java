package game.entities.actors;

import engine.core.Game;
import engine.input.KeyboardInput;
import engine.map.TiledMap;
import engine.physics.Collision;
import game.entities.Actor;
import game.entities.Entity;
import game.entities.behavior.Collidable;
import game.entities.behavior.Controllable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Moveable;
import game.entities.behavior.Swimmer;
import game.states.play.World;
import game.tiles.Tile;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;

public class Player extends Actor implements Collidable, Hittable, Controllable, Moveable, Swimmer {
    private World world;
    private final Image spriteSheet;
    private Image[][] animations;
    private int pixels; 


    // Animation
    private boolean up, down, left, right = false;
    private boolean moving;
    private double aniTimer;
    private int aniIndex;
    private final double aniSpeed = 0.2; // seconds per frame 
    private final int moveDown = 0, moveUp = 1, moveLeft = 2, moveRight = 3;
    private int playerAction = moveLeft;

     // Interaction
    private boolean interaction = false;


    public Player(Game game, World world) {
        super(game, game.getTileSize()*247, game.getTileSize()*250, game.getTileSize(), game.getTileSize(), 100);
        this.world = world;
        this.spriteSheet = new Image(getClass().getResource("/assets/actors/player/orc.png").toExternalForm());
        this.pixels = 32;
        loadAnimations();
        setSolidArea(pixels * 0.42,pixels * 0.85,pixels * 0.15,pixels * 0.08);
        setHitbox(0.3,0.5);
        setInteractArea(2, 2);
    }

    @Override
    public void update(double delta) {
        move(delta);
        updateAnimation(delta);
        setAnimationDirection();
    }

    @Override
    public void render(GraphicsContext g) {
        Image frame = animations[aniIndex][playerAction];

        int tileSize = (int) game.getTileSize();
        int tileX = (int) ((x + tileSize / 2) / tileSize);
        int tileY = (int) ((y + tileSize) / tileSize);

        Tile t = game.getTiledMap().getTile(tileX, tileY);

        // Swimming Y-offset from the Swimmer interface
        double offsetY = computeSwimOffsetY(game.getTiledMap(), x, y, tileSize);

        // --- normal draw if not in swimmable tile ---
        if (!(t instanceof game.tiles.behaviors.Swimmable)) {
            g.drawImage(frame, x, y + offsetY, width, height);
            return;
        }

        // --- if swimmable: clip bottom 70% of player sprite ---
        g.save();

        // Draw invisible "water surface" rectangle that hides sprite height
        double visibleHeight = height * 0.48;
        double clipY = y + offsetY;  // top of visible portion
        g.beginPath();
        g.rect(x, clipY, width, visibleHeight);
        g.closePath();
        g.clip();

        // Draw the player
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
        TiledMap map = game.getTiledMap();
        int tileSize = (int) game.getTileSize();

        // Try each direction only if not colliding
        if (up && !down && !collisionUp) {
            if (!collision.willCollideWithSolid(map, this, 0, -moveSpeed, tileSize, world.getEntities())) {
                dy -= moveSpeed;
                moving = true;
            }
        }
        if (down && !up && !collisionDown) {
            if (!collision.willCollideWithSolid(map, this, 0, moveSpeed, tileSize, world.getEntities())) {
                dy += moveSpeed;
                moving = true;
            }
        }
        if (left && !right && !collisionLeft) {
            if (!collision.willCollideWithSolid(map, this, -moveSpeed, 0, tileSize, world.getEntities())) {
                dx -= moveSpeed;
                moving = true;
            }
        }
        if (right && !left && !collisionRight) {
            if (!collision.willCollideWithSolid(map, this, moveSpeed, 0, tileSize, world.getEntities())) {
                dx += moveSpeed;
                moving = true;
            }
        }

        // Apply if no solid collision on the combined move
        if (!collision.willCollideWithSolid(map, this, dx, dy, tileSize, world.getEntities())) {
            x += dx;
            y += dy;
        }

        // Clamp inside world bounds
        double maxX = (map.getMapWidth()  * game.getTileSize()) - width;
        double maxY = (map.getMapHeight() * game.getTileSize()) - height;
        x = Math.max(0, Math.min(x, maxX));
        y = Math.max(0, Math.min(y, maxY));
    }


    private void updateAnimation(double delta) {
        if (moving) {
                aniTimer += delta;
                if (aniTimer >= aniSpeed) {
                    aniTimer = 0;
                    aniIndex++;
                    if (aniIndex >= animations.length) {
                    aniIndex = 1;
                    }
                }
        } else {
            aniIndex = 0;
        }
    }

    private void setAnimationDirection() {
        if (up)    playerAction = moveUp;
        if (down)  playerAction = moveDown;
        if (left)  playerAction = moveLeft;
        if (right) playerAction = moveRight;
    }

    @Override
    public double getBottomY() {
        if (solidArea != null) {
            return solidArea.getMaxY(); // already equals y + height
        }
        return y + height; // fallback
    }


    private void loadAnimations() {
        animations = new Image[3][4];
        for (int i = 0; i < animations.length; i++) {
            for (int j = 0; j < animations[i].length; j++) {
                PixelReader reader = spriteSheet.getPixelReader();
                animations[i][j] = new WritableImage(reader, i * pixels, j * pixels, pixels, pixels);
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


}
