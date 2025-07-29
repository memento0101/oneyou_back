package com.example.toygry.one_you.chat.dto;

import java.util.UUID;

public record ChatMessage(
        MessageType messageType,
        String roomId,
        UUID userId,
        String userName,
        String message) {
    public enum MessageType{
        ENTER, TALK, LEAVE
    }
}
