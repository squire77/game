package org.pdi.invaders.definition;

import org.pdi.invaders.InvadersGame;
import org.pdi.game.Picture;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class SpaceShip extends Picture implements MouseListener, MouseMotionListener {
    public SpaceShip(InvadersGame game) {
        super(game, triangle(25, 25));
        translate(350, 500);
        game.addMouseListener(this);
        game.addMouseMotionListener(this);
    }

    private void fireMissile() {
        game.createMissile(getCenter().x, (int) getCenter().y - 12);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Mouse clicked");
        fireMissile();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        final int MOVE_FACTOR = 2;

        if (e.getX() > getCenter().x) {
            translate(MOVE_FACTOR, 0);
        }
        if (e.getX() < getCenter().x) {
            translate(-MOVE_FACTOR, 0);
        }

        //System.out.println("Spaceship position" + getCenter());
    }
}
