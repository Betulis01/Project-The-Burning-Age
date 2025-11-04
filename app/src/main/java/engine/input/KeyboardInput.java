package engine.input;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.util.HashSet;
import java.util.Set;

public class KeyboardInput {
    private final Set<KeyCode> pressedKeys = new HashSet<>();
    private final Set<KeyCode> consumedKeys = new HashSet<>();

    public void onKeyPressed(KeyEvent e) {
        pressedKeys.add(e.getCode());
    }

    public void onKeyReleased(KeyEvent e) {
        pressedKeys.remove(e.getCode());
    }

    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }

    public void clear() {
        pressedKeys.clear();
    }

    public boolean consumeKey(KeyCode key) {
        if (pressedKeys.contains(key) && !consumedKeys.contains(key)) {
            consumedKeys.add(key);
            return true;
        }
        if (!pressedKeys.contains(key)) {
            consumedKeys.remove(key);
        }
        return false;
    }


}
