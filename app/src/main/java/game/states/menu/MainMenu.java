package game.states.menu;

import engine.core.Game;
import engine.input.MouseInput;
import engine.ui.Button;
import game.states.PlayState;
import game.states.menu.ui.Logo;
import javafx.application.Platform;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class MainMenu {
    private final Game game;
    private final double canvasWidth;
    private final double canvasHeight;
    private final Image playButton;
    private final Image optionsButton;
    private final Image quitButton;
    private final Image logoSpriteSheet;
    private final Image background;

    private Button play;
    private Button options;
    private Button quit;
    private Logo logo;

    public MainMenu(Game game) {
        this.game = game;

        canvasWidth = game.getCanvas().getWidth();
        canvasHeight = game.getCanvas().getHeight();

        playButton = new Image(getClass().getResource("/assets/ui/button/play_button.png").toExternalForm());
        optionsButton = new Image(getClass().getResource("/assets/ui/button/options_button.png").toExternalForm());
        quitButton = new Image(getClass().getResource("/assets/ui/button/quit_button.png").toExternalForm());
        logoSpriteSheet = new Image(getClass().getResource("/assets/ui/dragon_logo.png").toExternalForm());
        background = new Image(getClass().getResource("/assets/ui/background/background_big.jpg").toExternalForm());


        play = new Button(playButton, 3, canvasWidth / 2, canvasHeight / 2 + 200,33,16, 2);     // x, y example positions
        options = new Button(optionsButton, 3, (int)(canvasWidth / 2), canvasHeight / 2 + 300,33,16,2);
        quit = new Button(quitButton, 3,(int)(canvasWidth / 2), canvasHeight / 2 + 400,33,16,2);
        logo = new Logo(logoSpriteSheet, 0, 3, (int)(canvasWidth / 2), canvasHeight / 2 - 500);
    }

    // Called from MenuState.update()
    public void update(double delta) {
        // Example: handle animations, hover states, etc.
        // If nothing dynamic yet, leave empty.
        handleMouse();
    }

    // Called from MenuState.render(gc)
    public void render(GraphicsContext g) {
        g.clearRect(0, 0, canvasWidth, canvasHeight); // clears previous frame
        g.drawImage(background, 0, 0, canvasWidth, canvasHeight);
        
        play.render(g);
        options.render(g);
        quit.render(g);
        logo.render(g);
    
    }

private boolean lastHoverState = false;

private void handleMouse() {
    MouseInput mouse = game.getMouseInput();
    double mx = mouse.getMouseX();
    double my = mouse.getMouseY();

    // Determine hover BEFORE mutating anything
    boolean hoveringPlay = play.isHovered(mx, my);
    boolean hoveringOptions = options.isHovered(mx, my);
    boolean hoveringQuit = quit.isHovered(mx, my);

    boolean hovering = hoveringPlay || hoveringOptions || hoveringQuit;

    // Update cursor only when needed
    if (hovering != lastHoverState) {
        game.getScene().setCursor(hovering ? game.getClickCursor() : game.getDefaultCursor());
        lastHoverState = hovering;
    }

    // PLAY logic
    if (hoveringPlay) {
        play.setCurrentImage(mouse.isMousePressed() ? 2 : 1);
        if (mouse.consumeRelease()) game.changeState(new PlayState(game));
    } else play.setCurrentImage(0);

    // OPTIONS logic
    if (hoveringOptions) {
        options.setCurrentImage(mouse.isMousePressed() ? 2 : 1);
        if (mouse.consumeRelease()) {
            // TODO
        }
    } else options.setCurrentImage(0);

    // QUIT logic
    if (hoveringQuit) {
        quit.setCurrentImage(mouse.isMousePressed() ? 2 : 1);
        if (mouse.consumeRelease()) {
            Platform.runLater(() -> {
                game.stopRunning();
                game.getStage().close();
            });
        }
    } else quit.setCurrentImage(0);
}

}
