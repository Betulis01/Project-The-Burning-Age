package game.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class InteractButton {
    public static final int width = 16;
    public static final int height = 16;
    private static final int buttonWidth = 16;
    private static final int buttonHeight = 16;

    private Image spriteSheet;
    private Image[] frames;
    private final int frameCount = 1;
    private final int rowIndex = 0;
    private int currentFrame;
    private double x, y;

    public InteractButton(double x, double y) {
        this.x = x;
        this.y = y;
        this.spriteSheet = new Image(getClass().getResource("/assets/ui/button/e_button.png").toExternalForm());
        loadAnimations();
    }

    public void render(GraphicsContext g) {
        g.drawImage(frames[currentFrame], x, y, buttonWidth, buttonHeight);
    }

    private void loadAnimations() {
        frames = new Image[frameCount];
        PixelReader reader = spriteSheet.getPixelReader();
        for (int i = 0; i < frameCount; i++) { 
            frames[i] = new WritableImage(reader,
                    i * width,
                    rowIndex * height,
                    width,
                    height);
        }
    }

    public void setPosition(double wx, double wy) {
        x = wx;
        y = wy;
    }
    public void setFrame(int index) {
        currentFrame = index;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
