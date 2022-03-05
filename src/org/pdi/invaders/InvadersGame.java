package org.pdi.invaders;

import org.pdi.game.Game;
import org.pdi.invaders.definition.Alien;
import org.pdi.invaders.definition.Missile;
import org.pdi.invaders.definition.SpaceShip;

public class InvadersGame extends Game {
    protected void doSetup() {
        createSpaceShip();
        createAliens();
    }

    private void createSpaceShip() {
        addToGame(new SpaceShip(this));
    }

    private void createAliens() {
        final int spacing = 28;
        for (int j=0; j<4; ++j) {
            for (int i = 0; i < 5; ++i) {
                Alien alien = new Alien(this);
                alien.translate(i * 30, spacing * j);
                addToGame(alien);
            }
        }
    }

    public void createMissile(int x, int y) {
        if (MissileTracker.getInstance().getMissileCount() < 5) {
            addToGame(new Missile(this, x, y));
        }
    }
}
