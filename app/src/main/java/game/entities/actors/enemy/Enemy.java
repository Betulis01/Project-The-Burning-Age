package game.entities.actors.enemy;

import engine.core.Game;
import game.entities.actors.Actor;
import game.entities.behavior.Damageable;
import game.entities.behavior.Hittable;
import game.entities.behavior.Moveable;

public abstract class Enemy extends Actor implements Damageable, Moveable, Hittable {

    public Enemy(Game game) {
        super(game);
        //TODO Auto-generated constructor stub
    }
}
