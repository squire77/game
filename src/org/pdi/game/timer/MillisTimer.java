package org.pdi.game.timer;

import com.pd.modelcg.codegen.graphics.drawablecanvas.IDrawableCanvas;

import java.util.ArrayList;
import java.util.List;

public class MillisTimer {
    public MillisTimer(long waitTimeMillis) {
        this(waitTimeMillis, false, null);
    }

    public MillisTimer(long waitTimeMillis, boolean isFrameUpdateTimer, IDrawableCanvas canvas) {
        this.waitTimeMillis = waitTimeMillis;
        this.isFrameUpdateTimer = isFrameUpdateTimer;
        this.canvas = canvas;
        this.subscribers = new ArrayList<>();
        this.fpsTracker = new FpsTracker();
    }

    public void start() {
        startTimeMillis = System.currentTimeMillis();
    }

    public void update() {
        if (elapsed()) {
            if (isFrameUpdateTimer) {
                canvas.draw();
            } else {
                List<ITimerListener> listCopy = new ArrayList<>(subscribers);

                listCopy.forEach((listener) -> {
                    // make sure listener hasn't been removed
                    if (subscribers.contains(listener)) {
                        listener.tick();
                    }
                });
            }
        }
    }

    private boolean elapsed() {
        boolean result = false;

        long elapsedTime = System.currentTimeMillis() - startTimeMillis;

        if (elapsedTime > waitTimeMillis) {
            if (isFrameUpdateTimer) {
                fpsTracker.updateFps(elapsedTime);
            }

            startTimeMillis = System.currentTimeMillis();
            result = true;
        }

        return result;
    }

    public void subscribe(ITimerListener object) {
        subscribers.add(object);
    }
    public void unsubscribe(ITimerListener object) {
        subscribers.remove(object);
    }

    private class FpsTracker {
        public FpsTracker() {
            this.avgFPS = 60.0;
        }

        public void updateFps(long elapsedTime) {
            double currentFPS = 1000.0 / elapsedTime;
            avgFPS = (avgFPS +  currentFPS) / 2;
            //System.out.printf("%f.2 fps%n", avgFPS);
        }

        private double avgFPS;
    }

    private final long waitTimeMillis;
    private final boolean isFrameUpdateTimer;
    private final IDrawableCanvas canvas;
    private final List<ITimerListener> subscribers;
    private final FpsTracker fpsTracker;

    private long startTimeMillis;

}