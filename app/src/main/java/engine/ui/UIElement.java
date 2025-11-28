package engine.ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;

public abstract class UIElement {
    protected double x, y;
    protected int width, height;
    protected boolean visible = true;

    protected Image image;
    protected Image[] images;
    protected int frameCount;
    protected int currentImage;


    protected UIElement(Image image, int frameCount, double x, double y, int width, int height) {
        this.image = image;
        this.frameCount = frameCount;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        loadImages();
    }

    protected void loadImages() {
        if (image == null || frameCount <= 0) {
            images = new Image[]{ image };
            return;
        }

        images = new Image[frameCount];
        PixelReader reader = image.getPixelReader();
        for (int i = 0; i < frameCount; i++) {
            images[i] = new WritableImage(reader,i * width,0,width,height);
        }
    }

    public abstract void update(double delta);
    public abstract void render(GraphicsContext g);

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setCurrentImage(int currentImage) {
        this.currentImage = currentImage;
    }

}
