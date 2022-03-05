package org.pdi.game;

import com.pd.modelcg.codegen.graphics.drawablecanvas.Drawable;
import org.pdi.invaders.InvadersGame;

import java.awt.*;

abstract public class Picture extends Drawable {
    public Picture(InvadersGame game, Polygon polygon) {
        super(polygon);
        this.game = game;
        setColor(Color.black);
    }

    // these are like setup/teardown
    // do not call these methods directly; call game.addToGame/removeFromGame for double dispatch
    public void addToGame() {}
    public void removeFromGame() {}

    public static Polygon triangle(int base, int height) {
        int halfBase = base/2;
        int halfHeight = height/2;
        Polygon polygon = new Polygon();
        polygon.addPoint(0, -halfHeight);
        polygon.addPoint(halfBase, halfHeight);
        polygon.addPoint(-halfBase, halfHeight);
        return polygon;
    }

    public static Polygon square(int size) {
        return rectangle(size, size);
    }

    public static Polygon rectangle(int width, int height) {
        int halfWidth = width/2;
        int halfHeight = height/2;
        Polygon polygon = new Polygon();
        polygon.addPoint(-halfWidth, -halfHeight);
        polygon.addPoint(halfWidth, -halfHeight);
        polygon.addPoint(halfWidth, halfHeight);
        polygon.addPoint(-halfWidth, halfHeight);
        return polygon;
    }

    protected final InvadersGame game;
}
