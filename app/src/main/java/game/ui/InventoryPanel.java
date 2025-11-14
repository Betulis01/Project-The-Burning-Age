package game.ui;

import engine.ui.UIElement;
import javafx.scene.canvas.GraphicsContext;

//InventoryPanel has a Grid<UISlot>; each UISlot contains an ItemIcon and handles drag/drop logic.
// public class InventoryPanel extends UIElement {
//     // private Grid<UISlot> grid;
//     // private Inventory inventory;
//     // private InventoryController controller;

//     // public InventoryPanel(Image bg, int x, int y,
//     //                       int columns, int rows,
//     //                       Inventory inventory,
//     //                       InventoryController controller) {
//     //     super(bg, 1, x, y, (columns * UISlot.SLOT_SIZE), (rows * UISlot.SLOT_SIZE));
//     //     this.inventory = inventory;
//     //     this.controller = controller;
//     //     this.grid = new Grid<>(columns, rows, (slotIndex) -> 
//     //         new UISlot(slotIndex, x, y, this)
//     //     );
//     // }

//     // public void sync() {
//     //     for (UISlot slot : grid.getSlots()) {
//     //         ItemStack stack = inventory.getSlot(slot.getIndex());
//     //         slot.updateStack(stack);
//     //     }
//     // }

//     // @Override
//     // public void update(double delta) {
//     //     grid.update(delta);
//     // }

//     // @Override
//     // public void render(GraphicsContext g) {
//     //     if (!visible) return;
//     //     g.drawImage(image, x, y);
//     //     grid.render(g, x, y);
//     // }

//     // public void handleClick(double mx, double my) {
//     //     if (grid.tryClick(mx - x, my - y, controller)) {
//     //         sync();
//     //     }
//     // }
// }

