package game;

import game.entities.Item;

public class Inventory {
    private InventorySlot[] slots;
    
    public Inventory(int capacity) {
        slots = new InventorySlot[capacity];
        for (int i = 0; i < capacity; i++) {
            slots[i] = new InventorySlot();
        }
    }

    public boolean addItem(Item item) {
        // 1. Try stacking
        for (InventorySlot slot : slots) {
            if(slot.canStack(item)) {
                slot.addItem(item);
                return true;
            }
        }
        // 2. Add to empty slot
        for (InventorySlot slot : slots) {
            if (slot.isEmpty()) {
                slot.addItem(item);
                return true;
            }
        }
        return false; // Inventory full
    }
}
