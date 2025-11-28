
package engine.core;

import javafx.scene.canvas.GraphicsContext;

public abstract class GameState {
    protected final Game game;
    private final Object stateLock = new Object();

    public GameState(Game game) {
        this.game = game;
    }

    protected abstract void update(double delta);
    protected abstract void render(GraphicsContext gc);
    public abstract void unload();

    // synchronized entry points used by the engine
    public void safeUpdate(double delta) {
        synchronized (stateLock) {
            update(delta);
        }
    }

    public void safeRender(GraphicsContext gc) {
        synchronized (stateLock) {
            render(gc);
        }
    }
}
