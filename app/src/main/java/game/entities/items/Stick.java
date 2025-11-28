package game.entities.items;

import engine.core.Game;
import game.entities.Item;
import game.entities.behavior.Pickupable;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utilities.SpriteSheet;

public class Stick extends Item {

    public Stick(Game game, double x, double y) {
        super(game,x,y, 16, 16);

        SpriteSheet sheet = new SpriteSheet(new Image(getClass().getResource("/assets/items/Stick.png").toExternalForm()),16, 16);        
        this.image = sheet.getFrame(0, 0);
        this.maxStack = 4;
        setSolidArea(0,height*0.6,width,height*0.2);
        setPickUpArea(0, 0, width, height);
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
