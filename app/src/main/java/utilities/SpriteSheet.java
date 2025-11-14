package utilities;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public class SpriteSheet {
    private final Image sheet;
    private final int frameWidth;
    private final int frameHeight;
    private final PixelReader reader;

    public SpriteSheet(Image sheet, int frameWidth, int frameHeight) {
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.reader = sheet.getPixelReader();
    }

    public Image getFrame(int col, int row) {
        return new WritableImage(
            reader,
            col * frameWidth,
            row * frameHeight,
            frameWidth,
            frameHeight
        );
    }
}
