package game.ui.inventory;

import engine.core.Game;
import game.entities.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class InventoryUI {

    private final Game game;
    private final Inventory inventory;
    
    private Image spriteSheet;
    private Image[] images;
    private final int imageCount = 3;
    private int padding = 4;

    private final double slotSize = 32 * 2;
    private final double iconSize = 32 * 2;
    
    private int hoveredR = -1;
    private int hoveredC = -1;
    private int selectedR = -1;
    private int selectedC = -1;

    public InventoryUI(Game game, Inventory inventory) {
        this.game = game;
        this.inventory = inventory;

        spriteSheet = new Image(getClass().getResource("/assets/ui/inventory/inventorySlot.png").toExternalForm());    
        System.out.println(spriteSheet.getWidth() + "x" + spriteSheet.getHeight());

        loadImages();
    }

    public void update() {
        if (!inventory.isVisible()) return;

        var mouse = game.getMouseInput();
        double mx = mouse.getMouseX();
        double my = mouse.getMouseY();

        hoveredR = hoveredC = -1;

        for (int r = 0; r < inventory.getRows(); r++) {
            for (int c = 0; c < inventory.getCols(); c++) {

                double x = getSlotX(c);
                double y = getSlotY(r);

                if (mx >= x && mx <= x + getSlotSize() && my >= y && my <= y + getSlotSize()) {
                    hoveredR = r;
                    hoveredC = c;

                    if (mouse.consumePressed()) {
                        selectedR = r;
                        selectedC = c;
                    }
                }
            }
        }
    }

    public void render(GraphicsContext g) {
        if (!inventory.isVisible()) return;

        g.setImageSmoothing(false);

        for (int r = 0; r < inventory.getRows(); r++) {
            for (int c = 0; c < inventory.getCols(); c++) {

                double x = getSlotX(c);
                double y = getSlotY(r);

                drawSlot(g, x, y, r, c, r == hoveredR && c == hoveredC,r == selectedR && c == selectedC);

                Item item = inventory.getItem(r, c);
                if (item != null) {
                    g.drawImage(item.getImage(),x, y,getIconSize(), getIconSize());

                    int count = inventory.getCount(r, c);
                    if (count > 1) {
                        g.setFill(Color.WHITE);
                        g.setFont(Font.font(game.getPixelFont().getFamily(), 10)); 
                        g.fillText(String.valueOf(count), x + getSlotSize() * 0.7,y + getSlotSize() * 0.8);
                    }
                }
            }
        }
    }

private void drawSlot(GraphicsContext g, double x, double y,
                      int r, int c, boolean hovered, boolean selected) {

    boolean hasItem = inventory.getItem(r, c) != null;

    if (selected && hasItem) {
        g.drawImage(images[2], x, y, getSlotSize(), getSlotSize());
    } else if (hovered) {
        g.drawImage(images[1], x, y, getSlotSize(), getSlotSize());
    } else {
        g.drawImage(images[0], x, y, getSlotSize(), getSlotSize());
    }
}


    protected void loadImages() {
        images = new Image[imageCount];
        PixelReader reader = spriteSheet.getPixelReader();
        for (int i = 0; i < imageCount; i++) {
            images[i] = new WritableImage(reader, i * 32, 0, 32, 32);
        }
    }

    private double getSlotSize() {
        return slotSize;
    }

    private double getIconSize() {
        return iconSize;
    }

    private double getStartX() {
        return game.getVirtualWidth() * 0.8;
    }

    private double getStartY() {
        return game.getVirtualHeight() * 0.7;
    }

    private double getSlotX(int col) {
        return getStartX() + col * (getSlotSize() + padding);
    }

    private double getSlotY(int row) {
        return getStartY() + row * (getSlotSize() + padding);
    }
}
