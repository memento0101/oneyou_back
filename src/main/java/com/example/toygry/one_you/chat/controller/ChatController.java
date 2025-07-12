package com.example.toygry.one_you.chat.controller;

import com.example.toygry.one_you.chat.dto.ChatMessage;
import com.example.toygry.one_you.chat.dto.ChatUserInfo;
import com.example.toygry.one_you.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Set;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    // TODO 인증 정보에서 유저 정보 가져오도록 변경 필요 꼭꼭
    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat.addUser")
    public void addUser(ChatMessage chatMessage, StompHeaderAccessor headerAccessor) {
        String roomId = chatMessage.roomId();
        UUID userId = chatMessage.userId();
        String userName = chatMessage.userName();

        chatService.addUser(roomId, new ChatUserInfo(userId, userName));

        headerAccessor.getSessionAttributes().put("roomId", roomId);
        headerAccessor.getSessionAttributes().put("userId", userId);

        ChatMessage enterMessage = new ChatMessage(
                ChatMessage.MessageType.ENTER,
                roomId,
                userId,
                userName,
                userName + "님이 입장하셨습니다."
        );

        messagingTemplate.convertAndSend("/topic/" + roomId, enterMessage);
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage chatMessage) {
        messagingTemplate.convertAndSend("/topic/" + chatMessage.roomId(), chatMessage);
    }

    @MessageMapping("/chat.leaveUser")
    public void leaveUser(ChatMessage chatMessage) {
        chatService.removeUser(chatMessage.roomId(), chatMessage.userId());

        ChatMessage leaveMessage = new ChatMessage(
                ChatMessage.MessageType.LEAVE,
                chatMessage.roomId(),
                chatMessage.userId(),
                chatMessage.userName(),
                chatMessage.userName() + "님이 퇴장하셨습니다."
        );

        messagingTemplate.convertAndSend("/topic/" + chatMessage.roomId(), leaveMessage);
    }

    @GetMapping("/api/chat/users/{roomId}")
    @ResponseBody
    public Set<String> getUserNames(@PathVariable String roomId) {
        return chatService.getUsers(roomId);
    }
}
