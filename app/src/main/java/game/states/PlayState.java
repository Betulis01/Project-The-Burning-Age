package game.states;

import engine.core.Game;
import engine.core.GameState;
import engine.map.TiledMap;
import game.states.play.World;
import game.ui.Chat;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class PlayState extends GameState {
    private World world;
    protected Chat chat;

    public PlayState(Game game) {
        super(game);
        chat = new Chat(game);
    }

    @Override
    public void load() {
        // Load the raw map data
        var worldData = game.getTiledLoader().load("/maps/world.tmj");

        // Wrap into TiledMap
        TiledMap worldMap = new TiledMap(worldData);

        // Build the world using that map
        world = new World(game, worldMap);

        // UI
        chat = new Chat(game);
    }

    @Override
    public void update(double delta) {
        handleInput();
        if (world != null) {
            world.update(delta);
        }
    }

    @Override
    public void render(GraphicsContext g) {
        if (world != null) {
            world.render(g);
        }
        drawFpsAndUps(g);
    }

        

    @Override
    public void unload() {
        world = null;
    }

    private void handleInput() {
        if (game.getKeyboardInput().isKeyPressed(KeyCode.ESCAPE)) {
            game.changeState(new MenuState(game));
        }
        double scroll = game.getMouseInput().consumeScrollDeltaY();
        if (scroll != 0) {
            if (scroll > 0) world.getCamera().zoomIn(0.1);
            else world.getCamera().zoomOut(0.1);
        }
    }

    public void drawFpsAndUps(GraphicsContext g) {
        // --- HUD (screen space) ---
        g.setFill(javafx.scene.paint.Color.WHITE);
        g.fillText("FPS: " + game.getEngine().getFps(), 20, 40);
        g.fillText("UPS: " + game.getEngine().getUps(), 20, 20);
    }
}
