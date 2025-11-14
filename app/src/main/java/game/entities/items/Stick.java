package game.entities.items;

import engine.core.Game;
import game.entities.Entity;
import game.entities.Item;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import utilities.SpriteSheet;

public class Stick extends Item {
    private Image image;

    public Stick(Game game, double x, double y) {
        super(game,x,y, 16, 16);

        SpriteSheet sheet = new SpriteSheet(new Image(getClass().getResource("/assets/items/Stick.png").toExternalForm()),16, 16);        
        this.image = sheet.getFrame(0, 0);

        setSolidArea(0,height*0.6,width,height*0.2);
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
    public void onPickUp(Entity other) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onPickUp'");
    }

    @Override
    public boolean canPickUp() {
        return true;
    }
}
