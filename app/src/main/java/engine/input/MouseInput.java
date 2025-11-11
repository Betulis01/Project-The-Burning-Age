package engine.input;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

public class MouseInput {
    private double mouseX;
    private double mouseY;
    private boolean mousePressed;
    private boolean mouseReleased;
    private MouseButton pressedButton; // track which button
    private double scrollDeltaY;

    private final boolean[] buttonsDown = new boolean[MouseButton.values().length];



    public void onMouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public void onMousePressed(MouseEvent e) {
        mousePressed = true;
        mouseReleased = false;
        pressedButton = e.getButton();
        buttonsDown[e.getButton().ordinal()] = true; // track hold
    }

    public void onMouseReleased(MouseEvent e) {
        mouseReleased = true;
        mousePressed = false;
        buttonsDown[e.getButton().ordinal()] = false;
        pressedButton = null;
    }


    public void onScroll(ScrollEvent e) {
        scrollDeltaY = e.getDeltaY();
    }

    public double consumeScrollDeltaY() {
        double val = scrollDeltaY;
        scrollDeltaY = 0;
        return val;
    }

    public boolean consumePressed() {
        if (mousePressed) {
            mousePressed = false;
            return true;
        }
        return false;
    }

    public boolean consumeRelease() {
        if (mouseReleased) {
            mouseReleased = false;
            return true;
        }
        return false;
    }

    // New method for continuous button state
    public boolean isButtonDown(MouseButton button) {
        int i = button.ordinal();
        return i >= 0 && i < buttonsDown.length && buttonsDown[i];
    }


    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
    public boolean isMousePressed() { return mousePressed; }
    public boolean isMouseReleased() { return mouseReleased; }
    public MouseButton getPressedButton() { return pressedButton; }
}
