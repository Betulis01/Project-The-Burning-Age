package engine.ui;

import engine.input.events.Hoverable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Button extends UIElement implements Hoverable {
    private int scale;
    private boolean hovered = false;

    public Button(Image image, int frameCount, double x, double y, int width, int height, int scale) {
        super(image, frameCount, x, y, width, height);
        this.scale = scale;
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        if (!visible) return;
        g.drawImage(images[currentImage], x - (width * scale) / 2, y, width * scale, height * scale);
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        double scaledWidth = width * scale;
        double scaledHeight = height * scale;

        double left = x - scaledWidth / 2;
        double top = y;

        hovered = mouseX >= left && mouseX <= left + scaledWidth &&
                mouseY >= top && mouseY <= top + scaledHeight;

        return hovered;
    }

}
