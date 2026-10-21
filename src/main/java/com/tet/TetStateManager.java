package com.tet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TetStateManager {
    private static final Map<UUID, TetState> STATES = new HashMap<>();

    public static TetState get(UUID uuid) {
        return STATES.computeIfAbsent(uuid, k -> new TetState());
    }

    public static void remove(UUID uuid) {
        STATES.remove(uuid);
    }
}