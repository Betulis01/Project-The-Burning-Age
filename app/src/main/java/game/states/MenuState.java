package game.states;

import engine.core.Game;
import engine.core.GameState;
import game.states.menu.MainMenu;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;

public class MenuState extends GameState {
    private MainMenu state;

    public MenuState(Game game) {
        super(game);
    }

    @Override
    public void load() {
    state = new MainMenu(game); 
    }

    @Override
    public void update(double delta) {
        state.update(delta);
        handleInput();
    }

    @Override
    public void render(GraphicsContext g) {
        state.render(g);
    }

    @Override
    public void unload() {
    }

    private void handleInput() {
        if (game.getKeyboardInput().isKeyPressed(KeyCode.ESCAPE)) {
            game.changeState(new MenuState(game));
        }
    }
}
