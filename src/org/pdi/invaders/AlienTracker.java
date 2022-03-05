package org.pdi.invaders;

import org.pdi.invaders.definition.Alien;

import java.util.ArrayList;
import java.util.List;

public class AlienTracker {
    public static AlienTracker getInstance() {
        return instance;
    }

    private AlienTracker() {}

    public List<Alien> getAliens() {
        return this.alienList;
    }

    public void add(Alien alien) {
        alienList.add(alien);
    }

    public void remove(Alien alien) {
        alienList.remove(alien);
    }

    private static AlienTracker instance = new AlienTracker();
    private final List<Alien> alienList = new ArrayList<>();
}
