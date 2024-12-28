package com.example.miraclediscord.controller;

import com.example.miraclediscord.application.ChatRoomService;
import com.example.miraclediscord.dto.request.ChatRequest;
import com.example.miraclediscord.dto.response.ChatResponse;
import com.example.miraclediscord.dto.response.ChatResponse.Detail;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatRoomService chatRoomService;

    @GetMapping("/chat-rooms")
    public List<Detail> getAllChatRooms() {
        return chatRoomService.findAllChatRooms();
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatRequest.SendMessage request) {
        chatRoomService.sendMessage(request);
    }

    @MessageMapping("/chat.create")
    public ChatResponse.Detail createChatRoom(@Payload ChatRequest.CreateRoom request) {
        return chatRoomService.createChatRoom(request);
    }

    @MessageMapping("/chat.join")
    public void joinChatRoom(@Payload ChatRequest.JoinRoom request) {
        chatRoomService.joinChatRoom(request.getUserId(), request.getRoomId());
    }
}