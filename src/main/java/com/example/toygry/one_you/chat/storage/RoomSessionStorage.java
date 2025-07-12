package com.example.toygry.one_you.chat.storage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RoomSessionStorage {
    private final Map<String, Set<String>> roomUserMap = new ConcurrentHashMap<>();

    public void enterRoom(String roomId, String username) {
        roomUserMap.computeIfAbsent(roomId, key -> ConcurrentHashMap.newKeySet()).add(username);
    }

    public List<String> getUsers(String roomId) {
        return new ArrayList<>(roomUserMap.getOrDefault(roomId, Collections.emptySet()));
    }
}
