package game.ui;

import engine.core.Game;
import engine.input.KeyboardInput;
import game.entities.actors.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Chat {
    private final Game game;
    private boolean active;
    private final StringBuilder input = new StringBuilder();
    private String lastMessage = "";

    public Chat(Game game) {
        this.game = game;
    }
    
    public void toggle() {
        if (active) {
            lastMessage = input.toString().trim();
            input.setLength(0);
        }
        active = !active;
    }

    public void update(KeyboardInput keys) {
        if (!active) return;
        for (KeyCode code : KeyCode.values()) {
            if (keys.consumeKey(code)) {
                if (code.isLetterKey()) {
                    if (keys.isKeyPressed(KeyCode.SHIFT)) {
                        input.append(code.getName().toUpperCase());
                    } else {
                        input.append(code.getName().toLowerCase());
                    }
                } 
                else if (code == KeyCode.SPACE) input.append(" ");
                if (code == KeyCode.BACK_SPACE && input.length() > 0)
                    input.deleteCharAt(input.length() - 1);
            }
        }
    }

    public boolean isActive() { return active; }
    public String getLastMessage() { return lastMessage; }
    public void clearLastMessage() { lastMessage = ""; }

    public void render(GraphicsContext g, Player player) {
        double px = player.getX();
        double py = player.getY();
        if (!lastMessage.isEmpty()) {
            g.setFont(Font.font(game.getPixelFont().getFamily(), 6)); 
            g.setFill(Color.web("#b07409"));
            double textWidth = lastMessage.length() * 3; 
            g.fillText(lastMessage, px + (player.getWidth()/2) - (textWidth/2), py - 10);
        }

        if (active) {
            g.setFill(Color.WHITE);
            g.setFont(Font.font(game.getPixelFont().getFamily(), 6)); 
            g.fillText("> " + input, px, py + player.getHeight() + 10);
        }
    }
}
