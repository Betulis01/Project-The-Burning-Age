package game.states.menu.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class Logo {
    public static final int pixelWidth = 128;
    public static final int pixelHeight = 120;
    private static final int logoWidth = 128 * 3;
    private static final int logoHeight = 120 * 3;

    private final Image[] frames;
    private int currentFrame;
    private final double x, y;

    public Logo(Image spriteSheet, int rowIndex, int frameCount, double x, double y) {
        this.x = x;
        this.y = y;
        
        frames = new Image[frameCount];
        PixelReader reader = spriteSheet.getPixelReader();
        for (int i = 0; i < frameCount; i++) { 
            frames[i] = new WritableImage(reader,i * pixelWidth,rowIndex * pixelHeight,pixelWidth,pixelHeight);
        }
    }

    public void draw(GraphicsContext g) {
        for (int i = 0; i < frames.length; i++) {
            g.drawImage(frames[i], x - (logoWidth / 2), y, logoWidth, logoHeight);
        }     
    }

    public void setFrame(int index) {
        currentFrame = index;
    }
}
