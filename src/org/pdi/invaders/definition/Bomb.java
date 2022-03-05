package org.pdi.invaders.definition;

import org.pdi.invaders.InvadersGame;
import org.pdi.game.Picture;

import java.awt.*;

public class Bomb extends Picture {
    public Bomb(InvadersGame game) {
        super(game, new Polygon());
    }
}
