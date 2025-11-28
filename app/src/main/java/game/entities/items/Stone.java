package game.entities.items;

import engine.core.Game;
import game.entities.Item;
import game.entities.behavior.Pickupable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utilities.SpriteSheet;

public class Stone extends Item {

    public Stone(Game game, double x, double y) {
        super(game,x,y, 16, 16);

        SpriteSheet sheet = new SpriteSheet(new Image(getClass().getResource("/assets/items/stone.png").toExternalForm()),16, 16);        
        this.image = sheet.getFrame(0, 0);
        this.maxStack = 4;
        setSolidArea(width*0.45,height*0.5,width*0.2,height*0.1);
        setPickUpArea(width*0.45, height*0.5, width*0.2, height*0.2);
    }

    @Override
    public void update(double delta) {

    }

    @Override
    public void render(GraphicsContext g) {
        g.drawImage(image, x, y);
    }

    @Override
    public boolean isSolid() {
       return false;
    }

    @Override
    public void onPickUp(Pickupable other) {

    }

    @Override
    public boolean canPickUp() {
        return true;
    }
}
