package game.states.menu;

import engine.core.Game;
import engine.input.MouseInput;
import game.states.MenuState;
import game.states.PlayState;
import game.states.menu.ui.Button;
import game.states.menu.ui.Logo;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;


public class MainMenu extends MenuState{
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
        super(game);
        this.game = game;
        this.playButton = new Image(getClass().getResource("/assets/ui/play_button.png").toExternalForm());
        this.optionsButton = new Image(getClass().getResource("/assets/ui/options_button.png").toExternalForm());
        this.quitButton = new Image(getClass().getResource("/assets/ui/quit_button.png").toExternalForm());
        this.logoSpriteSheet = new Image(getClass().getResource("/assets/ui/dragon_logo.png").toExternalForm());
        this.background = new Image(getClass().getResource("/assets/ui/mountains_background.png").toExternalForm());
        this.canvasWidth = game.getCanvas().getWidth();
        this.canvasHeight = game.getCanvas().getHeight();

        play = new Button(playButton, 3, canvasWidth / 2, canvasHeight / 2 + 200,33,16);     // x, y example positions
        options = new Button(optionsButton, 3, (int)(canvasWidth / 2), canvasHeight / 2 + 300,33,16);
        quit = new Button(quitButton, 3,(int)(canvasWidth / 2), canvasHeight / 2 + 400,33,16);
        // logo = new Logo(logoSpriteSheet, 0, 3, (int)(canvasWidth / 2), canvasHeight / 2 - 500);
    }

    // Called from MenuState.update()
    @Override
    public void update(double delta) {
        // Example: handle animations, hover states, etc.
        // If nothing dynamic yet, leave empty.
        handleMouse();
    }

    // Called from MenuState.render(gc)
    @Override
    public void render(GraphicsContext g) {
        g.clearRect(0, 0, canvasWidth, canvasHeight); // clears previous frame
        g.drawImage(background, 0, 0, canvasWidth, canvasHeight);
        
        play.render(g);
        options.render(g);
        quit.render(g);
        //logo.render(g);
    
    }

    private void handleMouse() {
        MouseInput mouse = game.getMouseInput();
        double mx = mouse.getMouseX();
        double my = mouse.getMouseY();

        // --- PLAY ---
        if (play.isHovered(mx, my)) {
            play.setCurrentFrame(mouse.isMousePressed() ? 2 : 1);
            if (mouse.consumeRelease()) {
                play.setCurrentFrame(1);
                game.changeState(new PlayState(game)); 
            }
        } else {
            play.setCurrentFrame(0);
        }

        // --- OPTIONS ---
        if (options.isHovered(mx, my)) {
            options.setCurrentFrame(mouse.isMousePressed() ? 2 : 1);
            if (mouse.consumeRelease()) {
                options.setCurrentFrame(1);
                // TODO: add options logic later
            }
        } else {
            options.setCurrentFrame(0);
        }

        // --- QUIT ---
        if (quit.isHovered(mx, my)) {
            quit.setCurrentFrame(mouse.isMousePressed() ? 2 : 1);
            if (mouse.consumeRelease()) {
                quit.setCurrentFrame(1);
                Platform.runLater(() -> {
                    game.stopRunning();
                    game.getStage().close();
                    System.out.println("Quit clicked!");
                });
            }
        } else {
            quit.setCurrentFrame(0);
        }
    }
}
