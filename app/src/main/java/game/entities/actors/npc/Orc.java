package game.entities.actors.npc;

import engine.core.Game;
import engine.physics.Collision;
import game.entities.Entity;
import game.entities.actors.Actor;
import game.entities.behavior.Collidable;
import game.entities.behavior.Damageable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Moveable;
import game.maps.GameMap;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utilities.Utility;

public class Orc extends Actor implements Collidable, Damageable, Moveable, Hittable {
    private GameMap map;
    private final Image spriteSheet;
    private Image[][] animations;
    private int pixels; 

    private boolean up = false, down = false, left = false, right = false;
    private boolean moving;
    private double aniTimer;
    private int aniIndex;
    private final double aniSpeed = 0.2; // seconds per frame 


    // Animation states
    private int moveTimer;
    private final java.util.Random rand = new java.util.Random();
    private final int moveDown = 0, moveUp = 1, moveLeft = 2, moveRight = 3;
    private int orcAction = moveLeft;

    //Interaction
    private boolean interaction = false;

    public Orc(Game game, GameMap map, double x, double y) {
        super(game, game.getTileSize()*248, game.getTileSize()*251, 32, 32, 100);
        this.map = map;
        this.spriteSheet = new Image(getClass().getResource("/assets/actors/npc/orc.png").toExternalForm());
        this.pixels = 32;

        loadAnimations();
        setSolidArea(pixels * 0.42,pixels * 0.85,pixels * 0.15,pixels * 0.08);
        setHitbox(0.3,0.5);
        setInteractArea(1, 1);
    }

    @Override
    public void update(double delta) {
        if (!interaction) {
            move(delta);
            updateAnimation(delta);
            setAnimationDirection();
        } 
    }

    @Override
    public void render(GraphicsContext g) {
        
        Image frame = animations[aniIndex][orcAction];
        // Draw the player
        g.drawImage(frame, x, y, width, height);


        if (interaction) {
            String text = "Who you be?";
            g.setFont(Font.font(game.getPixelFont().getFamily(), 6)); 
            g.setFill(Color.web("#b07409"));
            double textWidth = text.length() * 3; 
            double drawX = x + (width/2) - (textWidth/2);
            double drawY = y - 10;

            g.fillText(text, drawX, drawY);
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

        moveTimer++;
        if (moveTimer >= Utility.randomNumberInterval(500, 1500)) {
            moveTimer = 0;
            int dir = rand.nextInt(10);
            up = down = left = right = false;
            switch (dir) {
                case 0 -> up = true;
                case 1 -> { up = true; left = true; }
                case 2 -> { up = true; right = true; }
                case 3 -> down = true;
                case 4 -> { down = true; left = true; }
                case 5 -> { down = true; right = true; }
                case 6 -> moving = false;
                case 7 -> moving = false;
                case 8 -> moving = false;
                case 9 -> moving = false;
            }
        }

        Collision collision = game.getCollision();
        int tileSize = (int) game.getTileSize();

        moving = up || down || left || right;
        // Try each direction only if not colliding
        if (up && !down && !collisionUp) {
            if (!collision.willCollideWithSolid(map, this, 0, -moveSpeed, tileSize, map.getEntities())) {
                y -= moveSpeed;
                moving = true;
            }
        }
        if (down && !up && !collisionDown) {
            if (!collision.willCollideWithSolid(map, this, 0, moveSpeed, tileSize, map.getEntities())) {
                y += moveSpeed;
                moving = true;
            }
        }
        if (left && !right && !collisionLeft) {
            if (!collision.willCollideWithSolid(map, this, -moveSpeed, 0, tileSize, map.getEntities())) {
                x -= moveSpeed;
                moving = true;
            }
        }
        if (right && !left && !collisionRight) {
            if (!collision.willCollideWithSolid(map, this, moveSpeed, 0, tileSize, map.getEntities())) {
                x += moveSpeed;
                moving = true;
            }
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
                    if (aniIndex >= animations.length) {
                    aniIndex = 1;
                    }
                }
        } else {
            aniIndex = 0;
        }
    }

    private void setAnimationDirection() {
        if (up)    orcAction = moveUp;
        if (down)  orcAction = moveDown;
        if (left)  orcAction = moveLeft;
        if (right) orcAction = moveRight;
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


    @Override
    public void takeDamage(int amount) {
        health -= amount;
        if (health < 0) health = 0;
    }

    @Override
    public boolean isDead() {
        return health <= 0;
    }

    // --Hittable--
    @Override
    public Rectangle2D getHitbox() {
        return hitbox; // or a slightly larger area if you want pickup overlap
    }
    @Override
    public void onHit(Hittable other) {
       //System.out.println("Orc hit!");
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
        if (interaction) {
            orcAction = moveDown;
        }
    }

    @Override
    public boolean canInteract() {
        return true;
    }

    //SETTERS
    public void setInteraction(boolean interaction) {
        this.interaction = interaction;
    }
}
