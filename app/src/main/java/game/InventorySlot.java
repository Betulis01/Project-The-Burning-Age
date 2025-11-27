package game;

import game.entities.Item;

public class InventorySlot {
    private Item item;
    private int count;
    
    public boolean isEmpty() {
        return item == null;
    }

    public boolean canStack(Item other) {
        return item != null && item.getClass() == other.getClass() && count < item.getMaxStack();
    }

    public void addItem(Item newItem) {
        if(item == null) {
            item = newItem;
        }
        count++;
    }
}
