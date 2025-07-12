package com.example.toygry.one_you.chat.service;

import com.example.toygry.one_you.chat.dto.ChatUserInfo;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final Map<String, Set<ChatUserInfo>> roomUsers = new ConcurrentHashMap<>();

    public void addUser(String roomId, ChatUserInfo chatUserInfo) {
        roomUsers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(chatUserInfo);
    }

    public void removeUser(String roomId, UUID userId) {
        Set<ChatUserInfo> users = roomUsers.get(roomId);
        if(users != null) {
            users.removeIf(user -> user.userId().toString().equals(userId.toString()));
            if (users.isEmpty()) {
                roomUsers.remove(roomId);
            }
        }
    }

    public Set<String> getUsers(String roomId) {
        return roomUsers.getOrDefault(roomId, Collections.emptySet())
                .stream()
                .map(ChatUserInfo::userName)
                .collect(Collectors.toSet());
    }
}
