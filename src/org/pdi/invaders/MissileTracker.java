package org.pdi.invaders;

public class MissileTracker {
    public static MissileTracker getInstance() {
        return instance;
    }

    private MissileTracker() {
        this.missileCount = 0;
    }

    public int getMissileCount() {
        return missileCount;
    }

    public boolean add() {
        if (missileCount < 5) {
            missileCount++;
            return true;
        } else {
            return false;
        }
    }

    public void remove() {
        System.out.println("decrement missile count");
        missileCount--;
    }

    private static MissileTracker instance = new MissileTracker();
    private int missileCount;
}
