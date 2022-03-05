package org.pdi.game.timer;

import com.pd.modelcg.codegen.graphics.drawablecanvas.IDrawableCanvas;

public class EventTimer implements Runnable {
    public EventTimer(IDrawableCanvas canvas) {
        this.frameUpdateTimer = new MillisTimer(15, true, canvas); // 15ms wait is 60fps
        this.oneSecondTimer = new MillisTimer(1000);
        this.thirtyMillisTimer = new MillisTimer(30);
        this.tenMillisTimer = new MillisTimer(10);
    }

    public MillisTimer getOneSecondTimer() { return this.oneSecondTimer; }
    public MillisTimer getThirtyMillisTimer() { return this.thirtyMillisTimer; }
    public MillisTimer getTenMillisTimer() { return this.tenMillisTimer; }

    public void run() {
        frameUpdateTimer.start();
        oneSecondTimer.start();
        thirtyMillisTimer.start();
        tenMillisTimer.start();

        while (true) {
            if (isStop()) {
                System.out.println("EventTimer exiting.");
                break;
            }

            frameUpdateTimer.update();
            oneSecondTimer.update();
            thirtyMillisTimer.update();
            tenMillisTimer.update();

            mySleep(1);
        }
    }

    private void mySleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    synchronized
    public boolean isStop() {
        return this.stop;
    }

    synchronized
    public void setStop() {
        this.stop = true;
    }

    private final MillisTimer frameUpdateTimer;
    private final MillisTimer oneSecondTimer;
    private final MillisTimer thirtyMillisTimer;
    private final MillisTimer tenMillisTimer;

    private boolean stop = false;
}
