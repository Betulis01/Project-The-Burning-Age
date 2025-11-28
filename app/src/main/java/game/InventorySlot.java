package game;

import game.entities.Item;

public class InventorySlot {
    private Item item;
    private int count;

    public boolean isEmpty() {
        return item == null;
    }

    public boolean canStack(Item other) {
        return item != null &&
               item.getClass() == other.getClass() &&
               count < item.getMaxStack();
    }

    public boolean addItem(Item newItem) {
        // Fill slot if empty
        if (item == null) {
            item = newItem;
            count = 1;
            return true;
        }

        // Try stacking
        if (canStack(newItem)) {
            count++;
            return true;
        }
        return false; // slot full or wrong type
    }

    public void removeOne() {
        if (isEmpty()) return;
        count--;
        if (count <= 0) {
            item = null;
            count = 0;
        }
    }

    public Item getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }
}
