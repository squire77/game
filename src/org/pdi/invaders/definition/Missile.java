package org.pdi.invaders.definition;

import org.pdi.invaders.AlienTracker;
import org.pdi.invaders.InvadersGame;
import org.pdi.invaders.MissileTracker;
import org.pdi.game.Picture;
import org.pdi.game.timer.ITimerListener;

import java.awt.Color;

public class Missile extends Picture implements ITimerListener {
    public Missile(InvadersGame game, int x, int y) {
        super(game, rectangle(2,15));
        setColor(Color.RED);
        translate(x, y);
    }

    @Override
    public void addToGame() {
        MissileTracker.getInstance().add();
        game.getEventTimer().getTenMillisTimer().subscribe(this);
    }

    @Override
    public void removeFromGame() {
        MissileTracker.getInstance().remove();
        game.getEventTimer().getTenMillisTimer().unsubscribe(this);
    }

    @Override
    public void tick() {
        if (validate(0,-2, game.getWidth(), game.getHeight())) {
            //System.out.println("Missile on the move");
            translate(0, -3);

            for (Alien alien : AlienTracker.getInstance().getAliens()) {
                if (alien.checkForCollision(this)) {
                    game.removeFromGame(this);
                    break; // only kill 1 alien at a time
                }
            }
        } else {
            game.removeFromGame(this);
        }
    }
}
