package org.pdi.invaders.definition;

import org.pdi.game.ICollisionDetector;
import org.pdi.invaders.AlienTracker;
import org.pdi.invaders.InvadersGame;
import org.pdi.game.Picture;
import org.pdi.game.timer.ITimerListener;
import org.pdi.game.timer.MillisTimer;

import java.awt.*;

public class Alien extends Picture implements ITimerListener, ICollisionDetector {
    static int counter = 100;
    private final String name;
    private int direction;
    private TimerSubscriber timeSubscriber;

    public Alien(InvadersGame game) {
        super(game, square(25));
        this.name = Integer.toString(counter++);
        this.direction = 1;
        setColor(Color.BLUE);
        translate(50, 150);

        this.timeSubscriber = new TimerSubscriber(game, this);
    }

    private class TimerSubscriber {
        public TimerSubscriber(InvadersGame game, Alien alien) {
            this.game = game;
            this.alien = alien;
            this.alreadyFaster = false;
            this.currentTimer = game.getEventTimer().getThirtyMillisTimer();
        }

        public void subscribe() {
            currentTimer.subscribe(alien);
        }

        public void unsubscribe() {
            currentTimer.unsubscribe(alien);
        }

        public void goFaster() {
            if (!alreadyFaster) {
                unsubscribe();
                currentTimer = game.getEventTimer().getTenMillisTimer();
                subscribe();
                alreadyFaster = true;
            }
        }

        private final InvadersGame game;
        private final Alien alien;
        private boolean alreadyFaster;
        private MillisTimer currentTimer;
    }

    @Override
    public void addToGame() {
        AlienTracker.getInstance().add(this);
        timeSubscriber.subscribe();
    }

    @Override
    public void removeFromGame() {
        AlienTracker.getInstance().remove(this);
        timeSubscriber.unsubscribe();
    }

    @Override
    public void tick() {
        int deltaX = 2 * direction;

        if (validate(deltaX, 0, game.getWidth(), game.getHeight())) {
            //System.out.println("Alien on the move");
            translate(deltaX, 0);

            if (name.equals("102")) {
                rotate(-5);
            } else {
                rotate(10);
            }
        } else {
            translate(0, 28);
            if (getCenter().y > 1000) {
                timeSubscriber.goFaster();
            }
            timeSubscriber.goFaster();
            direction *= -1;
        }
    }

    @Override
    public boolean checkForCollision(Picture picture) {
        if (intersects(picture)) {
            System.out.println("Dead alien: " + name);
            game.removeFromGame(this);
            return true;
        }

        return false;
    }
}
