package game.entities.actors.enemy;

import engine.core.Game;
import game.entities.actors.Actor;
import game.entities.behavior.Collidable;
import game.entities.behavior.Damageable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Moveable;

public abstract class Enemy extends Actor implements Collidable, Damageable, Moveable, Hittable {

    public Enemy(Game game, double x, double y, double width, double height, double speed) {
        super(game, x, y, width, height, speed);
        //TODO Auto-generated constructor stub
    }
}
