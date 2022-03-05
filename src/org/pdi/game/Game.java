package org.pdi.game;

import com.pd.modelcg.codegen.graphics.drawablecanvas.IDrawableCanvas;
import org.pdi.game.timer.EventTimer;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public class Game {
    public void setup(IDrawableCanvas canvas) {
        this.canvas = canvas;
        this.eventTimer = new EventTimer(canvas);

        doSetup();
        new Thread(eventTimer).start();
        Runtime.getRuntime().addShutdownHook(new Thread(eventTimer::setStop));
    }

    public EventTimer getEventTimer() { return this.eventTimer; }

    protected void doSetup() {}

    public int getWidth() {
        return canvas.getWidth();
    }

    public int getHeight() {
        return canvas.getHeight();
    }

    public void addToGame(Picture picture) {
        canvas.addObject(picture);
        picture.addToGame();
    }

    public void removeFromGame(Picture picture) {
        canvas.removeObject(picture);
        picture.removeFromGame();
    }

    public void addMouseListener(MouseListener listener) {
        canvas.addMouseListener(listener);
    }

    public void addMouseWheelListener(MouseWheelListener listener) {
        canvas.addMouseWheelListener(listener);
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        canvas.addMouseMotionListener(listener);
    }

    protected IDrawableCanvas canvas;
    protected EventTimer eventTimer;
}
