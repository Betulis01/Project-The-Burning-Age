package game.states;

import engine.core.Game;
import engine.core.GameState;
import engine.map.TiledMap;
import game.entities.Item;
import game.entities.actors.Player;
import game.states.play.World;
import game.ui.Chat;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

public class PlayState extends GameState {
    private World world;
    protected Player player;
    protected Chat chat;

    public PlayState(Game game) {
        super(game);
        chat = new Chat(game);

        // 1. Create player 
        player = new Player(game);

        // 2. Load map
        var worldData = game.getTiledLoader().load("/maps/world.tmj");
        TiledMap worldMap = new TiledMap(worldData);

        // 3. Create world with player
        world = new World(game, worldMap, player);

        // 4. Inject world (GameMap) into player
        player.setMap(world);

        // UI
        chat = new Chat(game);
    }


    @Override
    public void update(double delta) {

        handleInput();

        // World always updates — or pause world if inventory open
        if (world != null) {
            world.update(delta);
        }
    }


    @Override
    public void render(GraphicsContext g) {
        if (world != null) {
            world.render(g);
        }

        //Chat
        chat.render(g, player);
        
        //Inventory
        renderInventory(g);

        //FPS and UPS
        drawFpsAndUps(g);
    }

        

    @Override
    public void unload() {
        world = null;
    }

    private void handleInput() {
        var keys = game.getKeyboardInput();

        // CHAT toggle first
        if (keys.consumeKey(KeyCode.ENTER)) {
            chat.toggle();
        }

        //Zoom
        double scroll = game.getMouseInput().consumeScrollDeltaY();
        if (scroll != 0) {
            if (scroll > 0) world.getCamera().zoomIn(0.1);
            else world.getCamera().zoomOut(0.1);
        }

        // INVENTORY toggle
        if (!player.getInventory().isVisible() && keys.consumeKey(KeyCode.TAB)) {
            player.getInventory().setVisible(true);
        } else if (player.getInventory().isVisible() && keys.consumeKey(KeyCode.TAB)) {
            player.getInventory().setVisible(false);
        }

        // Only control player when chat & inventory are BOTH closed
        if (!chat.isActive()) {
            player.handleInput();
        }
    }

    
    private void renderInventory(GraphicsContext g) {
        if (!player.getInventory().isVisible()) return;
        
        int baseIconSize = 16;  // original art resolution
        double uiScale = game.getScale(); // draw it scaled for clarity
        double slotSize = baseIconSize * uiScale;  // = 32px

        int slotSpacing = 36; // slotSize + border
        int startX = 20;
        int startY = 20;

        for (int r = 0; r < player.getInventory().getRows(); r++) {
            for (int c = 0; c < player.getInventory().getCols(); c++) {
                int x = startX + c * slotSpacing;
                int y = startY + r * slotSpacing;

                // draw slot background
                g.setStroke(javafx.scene.paint.Color.RED);
                g.setLineWidth(2);
                g.strokeRect(x, y, slotSpacing, slotSpacing);

                Item item = player.getInventory().getItem(r, c);
                if (item == null) continue;

                Image icon = item.getImage();
                if (icon != null) {
                    // keep nearest-neighbor scaling for crisp pixel art
                    g.setImageSmoothing(false);

                    // draw centered inside slot
                    g.drawImage(icon,
                        x + 2, y + 2,
                        slotSize, slotSize);
                }

                // Draw stack count if >1
                int count = player.getInventory().getCount(r, c);
                if (count > 1) {
                    g.setFill(javafx.scene.paint.Color.WHITE);
                    g.setFont(Font.font(game.getPixelFont().getFamily(), 10)); // 20px size
                    g.fillText(String.valueOf(count), x + slotSpacing - 12, y + slotSpacing - 4);
                }
            }
        }
    }



    public void drawFpsAndUps(GraphicsContext g) {
        // --- HUD (screen space) ---
        g.setFill(javafx.scene.paint.Color.WHITE);
        g.setFont(Font.font(game.getPixelFont().getFamily(), 20)); // 20px size
        g.fillText("FPS: " + game.getEngine().getFps(), 20, 40);
        g.fillText("UPS: " + game.getEngine().getUps(), 20, 20);
    }


    public Chat getChat() {
        return chat;
    }
}
