package game.ui.inventory;

import game.entities.items.Item;

public class Inventory {
    private final InventorySlot[][] slots;
    private final int rows;
    private final int cols;

    private boolean visible = false;

    public Inventory(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.slots = new InventorySlot[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                slots[r][c] = new InventorySlot();
            }
        }
    }

    public boolean addItem(Item item) {
        // 1. Try stacking
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (slots[r][c].canStack(item)) {
                    slots[r][c].addItem(item);
                    return true;
                }
            }
        }
        // 2. Fill empty slot
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (slots[r][c].isEmpty()) {
                    slots[r][c].addItem(item);
                    return true;
                }
            }
        }
        return false; // full
    }

    public void clearSlot(int r, int c) {
    slots[r][c].clear();
    }

    public void setItem(int r, int c, Item item, int count) {
        slots[r][c].set(item, count);
    }


    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public InventorySlot getSlot(int r, int c) {
        return slots[r][c];
    }

    public Item getItem(int r, int c) {
        return slots[r][c].getItem();
    }

    public int getCount(int r, int c) {
        return slots[r][c].getCount();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
