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
    protected Image[] frames;
    protected int frameCount;
    protected int currentFrame;



    protected UIElement(Image image, int frameCount, double x, double y, int width, int height) {
        this.image = image;
        this.frameCount = frameCount;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        loadFrames();
    }

    protected void loadFrames() {
        if (image == null || frameCount <= 0) {
            frames = new Image[]{ image };
            return;
        }

        frames = new Image[frameCount];
        PixelReader reader = image.getPixelReader();

        for (int i = 0; i < frameCount; i++) {
            frames[i] = new WritableImage(reader,i * width,0,width,height);
        }
    }

    public abstract void update(double delta);
    public abstract void render(GraphicsContext g);

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }

}
