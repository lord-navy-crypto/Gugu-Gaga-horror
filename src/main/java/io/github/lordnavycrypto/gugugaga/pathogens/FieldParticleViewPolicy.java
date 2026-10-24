package io.github.lordnavycrypto.gugugaga.pathogens;

import java.util.ArrayList;
import java.util.List;

/**
 * Pure, non-biological policy for the opt-in game-data display.
 * Coordinates, frame scheduling, and camera distance are display-only.
 * Does not inspect infection, host susceptibility or transmission.
 */
public final class FieldParticleViewPolicy {
    private FieldParticleViewPolicy() {}
    public static final int CELL_SIZE = 4;
    public static final double MAX_SURFACE_DISTANCE = 12.0;
    public static final double MAX_ANIMAL_DISTANCE = 8.0;

    public static boolean insideCell(int blockX, int blockZ, int cellX, int cellZ) {
        return Math.floorDiv(blockX, CELL_SIZE) == cellX
                && Math.floorDiv(blockZ, CELL_SIZE) == cellZ;
    }

    public static boolean surfaceInView(double observerY, int surfaceY) {
        return Math.abs(observerY - surfaceY) <= MAX_SURFACE_DISTANCE;
    }

    public static boolean animalInView(double observerY, double animalY) {
        return Math.abs(observerY - animalY) <= MAX_ANIMAL_DISTANCE;
    }

    /** At most maxPerFrame distinct observers per round-robin frame. */
    public static List<Integer> viewerSlots(int online, int maxPerFrame, long frame) {
        if (online < 0 || maxPerFrame < 1 || frame < 0)
            throw new IllegalArgumentException("Invalid cosmetic viewer budget");
        if (online == 0) return List.of();
        int take = Math.min(online, maxPerFrame);
        int begin = (int) ((Math.floorMod(frame, online) * maxPerFrame) % online);
        List<Integer> slots = new ArrayList<>(take);
        for (int i = 0; i < take; i++) slots.add((begin + i) % online);
        return List.copyOf(slots);
    }
}
