package game.states.play;

import engine.core.Game;
import engine.core.GameState;
import engine.map.TiledMap;
import game.entities.actors.player.Player;
import game.maps.world.World;
import game.states.menu.MenuState;
import game.ui.Chat;
import game.ui.inventory.InventoryUI;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;

public class PlayState extends GameState {
    private final double canvasWidth;
    private final double canvasHeight;
    private World world;
    protected Player player;
    protected InventoryUI inventoryUI;
    protected Chat chat;

    public PlayState(Game game) {
        super(game);
        this.canvasWidth = game.getCanvas().getWidth();
        this.canvasHeight = game.getCanvas().getHeight();
        
        // 1. Create player 
        player = new Player(game);
        inventoryUI = new InventoryUI(game, player.getInventory());

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
        // World always updates — or pause world if inventory open
        if (world != null) {
            world.update(delta);
        }   

        //Input
        handleInput();

        //Inventory
        inventoryUI.update();
        
    }


    @Override
    public void render(GraphicsContext g) {
        if (world != null) {
            world.render(g);
        }

        //Chat
        chat.render(g, player);
        
        //Inventory
        inventoryUI.render(g);

        //FPS and UPS
        drawFpsAndUps(g);
    }

        

    @Override
    public void unload() {
        world = null;
    }

    private void handleInput() {
        game.getScene().setCursor(game.getDefaultCursor());
        var keys = game.getKeyboardInput();


        if (game.getKeyboardInput().isKeyPressed(KeyCode.ESCAPE)) {
            game.changeState(new MenuState(game));
        }

        // CHAT toggle first
        if (keys.consumeKey(KeyCode.ENTER)) {
            chat.toggle();
        }

        // INVENTORY toggle
        if (!player.getInventory().isVisible() && keys.consumeKey(KeyCode.TAB)) {
            player.getInventory().setVisible(true);
        } else if (player.getInventory().isVisible() && keys.consumeKey(KeyCode.TAB)) {
            player.getInventory().setVisible(false);
        }


    }

    public void drawFpsAndUps(GraphicsContext g) {
        // --- HUD (screen space) ---
        g.setFill(javafx.scene.paint.Color.WHITE);
        g.setFont(Font.font(game.getPixelFont().getFamily(), 20)); 
        g.fillText("FPS: " + game.getEngine().getFps(), 20, 40);
        g.fillText("UPS: " + game.getEngine().getUps(), 20, 20);
    }


    public Chat getChat() {
        return chat;
    }
}
