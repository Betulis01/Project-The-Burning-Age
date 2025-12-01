package engine.core;


import engine.input.KeyboardInput;
import engine.input.MouseInput;
import engine.map.EntityRegistry;
import engine.map.TiledMap;
import engine.map.TiledMapLoader;
import engine.physics.Collision;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utilities.Utility;

public class Game {
    // Core systems
    private final Engine engine;
    private MouseInput mouseInput;
    private KeyboardInput keyboardInput;
    private TiledMap tiledMap;
    private TiledMapLoader tiledLoader;
    private GraphicsContext g;
    private Collision collision;
    private Utility utility;
    private EntityRegistry registry;

    // World + display settings
    private final double originalTileSize = 32;
    private double tileSize;      // scaled size in screen pixels
    private static double scale;
    private double deviceScale;   // screen → logical scale
    private double virtualWidth = 1920;
    private double virtualHeight = 1080;
    private double screenWidth;
    private double screenHeight;

    private final Stage stage;
    private final Canvas canvas;
    private final Scene scene;
    private GameState currentState;
    private boolean running = true;

    //Font and Cursor
    private final Font pixelFont = Font.loadFont(getClass().getResourceAsStream("/ui/fonts/minecraft_font.ttf"), 0);
    private final Image defaultCursorImage = new Image(getClass().getResource("/assets/ui/cursor/cursor.png").toExternalForm());
    private ImageCursor defaultCursor;
    private final Image clickCursorImage = new Image(getClass().getResource("/assets/ui/cursor/click.png").toExternalForm());
    private ImageCursor clickCursor;

    public Game(Engine engine, Stage stage, double width, double height) {
        this.engine = engine;
        this.stage = stage;
        this.screenWidth = width;
        this.screenHeight = height;

        computeDeviceScale(virtualWidth, virtualHeight);
        initTileScaling();
        
        this.canvas = new Canvas(screenWidth, screenHeight);
        this.scene = new Scene(new javafx.scene.layout.Pane(canvas));
        this.g = canvas.getGraphicsContext2D();
        g.setImageSmoothing(false);
        stage.setScene(scene);
        stage.show();
        
        System.out.println("Game initialized at " + screenWidth + "x" + screenHeight + " (scale=" + String.format("%.2f", deviceScale) + "). Tilesize: " + tileSize);

        initSystems();
    }

    // --- display scaling ---
    private void computeDeviceScale(double targetVirtualWidth, double targetVirtualHeight) {
        double scaleX = screenWidth / targetVirtualWidth;
        double scaleY = screenHeight / targetVirtualHeight;
        deviceScale = Math.min(scaleX, scaleY);
    }

    private void initTileScaling() {
        // scale tile to match display scaling; adjustable multiplier if you want larger sprites
        tileSize = originalTileSize * deviceScale;
        scale = tileSize / originalTileSize;
    }

    // --- core setup ---
    private void initSystems() {
        initInputs();
        tiledLoader = new TiledMapLoader();
        registry = new EntityRegistry();
        collision = new Collision();
        utility = new Utility();

        //cursor
        loadCursors();
        scene.setCursor(defaultCursor);
    }

    private void initInputs() {
        mouseInput = new MouseInput();
        keyboardInput = new KeyboardInput();
        canvas.setOnMouseMoved(mouseInput::onMouseMoved);
        canvas.setOnMousePressed(mouseInput::onMousePressed);
        canvas.setOnMouseReleased(mouseInput::onMouseReleased);
        canvas.setOnMouseMoved(mouseInput::onMouseMoved);
        canvas.setOnMouseDragged(mouseInput::onMouseMoved);
        canvas.setOnScroll(mouseInput::onScroll);
        canvas.setOnKeyPressed(keyboardInput::onKeyPressed);
        canvas.setOnKeyReleased(keyboardInput::onKeyReleased);
        canvas.setFocusTraversable(true);
        canvas.requestFocus();
    }

    // --- loop interaction ---
    public void update(double delta) {
        if (currentState != null) currentState.safeUpdate(delta);
    }

    public void render(GraphicsContext gc) {
        if (currentState != null) currentState.safeRender(gc);
    }

    public void changeState(GameState newState) {
        if (currentState != null) currentState.unload();
        currentState = newState;
        //currentState.load();
    }

    // --- setters ---
    public void setTiledMap(TiledMap map) {
        this.tiledMap = map;
    }

    //Cursor
    public void loadCursors() {
        defaultCursor = new ImageCursor(defaultCursorImage, 0,0);
        clickCursor = new ImageCursor(clickCursorImage,0,0);
    }

    // --- getters ---
    public double getTileSize() { return tileSize; }
    public double getDeviceScale() { return deviceScale; }
    public double getVirtualWidth() { return virtualWidth; }
    public double getVirtualHeight() { return virtualHeight; }
    public double getScreenWidth() { return screenWidth; }
    public double getScreenHeight() { return screenHeight; }
    public double getOriginalTileSize() { return originalTileSize; }
    public Canvas getCanvas() { return canvas; }
    public Scene getScene() { return scene; }
    public GraphicsContext getGraphicsContext() { return g; }
    public Stage getStage() { return stage; }

    public boolean isRunning() { return running; }
    public void stopRunning() { running = false; }
    
    public Engine getEngine() {return engine;}
    public MouseInput getMouseInput() { return mouseInput; }
    public KeyboardInput getKeyboardInput() { return keyboardInput; }
    public TiledMapLoader getTiledLoader() { return tiledLoader; }
    public TiledMap getTiledMap() { return tiledMap; }
    public Utility getUtility() {return utility;}

    public Collision getCollision() {
        return collision;
    }

    public Font getPixelFont() {
        return pixelFont;
    }

    public EntityRegistry getRegistry() {
        return registry;
    }

    public static double getScale() {
        return scale;
    }

    public ImageCursor getClickCursor() {
        return clickCursor;
    }

    public ImageCursor getDefaultCursor() {
        return defaultCursor;
    }
}
