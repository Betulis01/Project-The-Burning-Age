package game.states.menu.ui;

import engine.input.events.Hoverable;
import engine.ui.UIElement;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Button extends UIElement implements Hoverable {
    private boolean hovered = false;

    public Button(Image image, int frameCount, double x, double y, int width, int height) {
        super(image, frameCount, x, y, width, height);
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        if (!visible) return;
        g.drawImage(frames[currentFrame], x - width / 2, y, width, height);
    }

    @Override
    public boolean isHovered(double mouseX, double mouseY) {
        double left = x - width / 2.0;
        double top = y;
        hovered = mouseX >= left && mouseX <= left + width &&
                  mouseY >= top && mouseY <= top + height;
        return hovered;
    }
}
