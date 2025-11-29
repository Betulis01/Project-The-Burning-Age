package engine.core;

import java.util.concurrent.atomic.AtomicBoolean;

import game.states.menu.MenuState;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Engine extends Application {
    public int fps;
    public int ups;

    @Override
    public void start(Stage stage) {
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        double screenWidth = screenBounds.getWidth();
        double screenHeight = screenBounds.getHeight();

        Pane root = new Pane();
        Scene scene = new Scene(root, screenWidth, screenHeight);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setFullScreen(true);
        stage.setScene(scene);

        System.out.println("Game starting...");
        Game game = new Game(this, stage, screenWidth, screenHeight);
        game.changeState(new MenuState(game)); // <-- create state first
        startGameThread(game);    // <-- then start loop  
    }



    private void startGameThread(Game game) {
        AtomicBoolean renderPending = new AtomicBoolean(false);

        Thread gameThread = new Thread(() -> {
            long lastTime = System.nanoTime();
            long fpsTimer = System.currentTimeMillis();
            int frames = 0;
            int updates = 0;

            while (game.isRunning()) {
                long now = System.nanoTime();
                double delta = (now - lastTime) / 1e9;
                if (delta > 0.1) delta = 0.1;
                lastTime = now;

                game.update(delta); // UPDATE
                updates++;

                if (renderPending.compareAndSet(false, true)) {
                    Platform.runLater(() -> {
                        GraphicsContext g = game.getGraphicsContext();
                        game.render(g);  // RENDER
                        renderPending.set(false);
                    });
                    frames++;
                }


                if (System.currentTimeMillis() - fpsTimer >= 1000) {
                    fps = frames;
                    ups = updates;
                    frames = 0;
                    updates = 0;
                    fpsTimer += 1000;
                }

                try {
                    Thread.sleep(2); // small delay to prevent busy-wait
                } catch (InterruptedException ignored) {}
            }
        }, "GameThread");

        gameThread.setDaemon(true);
        gameThread.start();
    }

    public int getFps() {
        return fps;
    }

    public int getUps() {
        return ups;
    }


    public static void main(String[] args) {
        launch(args);
    }



 
}
