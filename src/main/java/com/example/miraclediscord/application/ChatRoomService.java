package com.example.miraclediscord.application;

import com.example.miraclediscord.dto.request.ChatRequest;
import com.example.miraclediscord.dto.response.ChatResponse;
import com.example.miraclediscord.dto.response.ChatResponse.Detail;
import com.example.miraclediscord.model.entity.chat.ChatMessage;
import com.example.miraclediscord.model.entity.chat.ChatRoom;
import com.example.miraclediscord.model.entity.user.User;
import com.example.miraclediscord.model.repository.ChatMessageRepository;
import com.example.miraclediscord.model.repository.ChatRoomRepository;
import com.example.miraclediscord.model.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    // ... 기존 의존성 주입
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public List<ChatResponse.Detail> findAllChatRooms() {
        return chatRoomRepository.findAll().stream()
            .map(ChatResponse.Detail::from)
            .collect(Collectors.toList());
    }

    @Transactional
    public ChatResponse.Detail createChatRoom(ChatRequest.CreateRoom request) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(request.getName());
        chatRoom.setType(request.getType());

        // 참가자 추가
        Set<User> participants = request.getParticipantIds().stream()
            .map(userId -> userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found")))
            .collect(Collectors.toSet());

        chatRoom.setParticipants(participants);

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        // 방 생성 알림
        ChatResponse.Detail responseDto = ChatResponse.Detail.from(savedRoom);
        messagingTemplate.convertAndSend("/topic/rooms", responseDto);

        return responseDto;
    }

    @Transactional
    public void sendMessage(ChatRequest.SendMessage request) {
        ChatRoom chatRoom = chatRoomRepository.findById(request.getRoomId())
            .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User sender = userRepository.findById(request.getSenderId())
            .orElseThrow(() -> new RuntimeException("User not found"));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoom(chatRoom);
        chatMessage.setSender(sender);
        chatMessage.setContent(request.getContent());

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);

        // 메시지 변환 및 전송
        ChatResponse.MessageDetail messageDto = ChatResponse.MessageDetail.from(savedMessage);
        messagingTemplate.convertAndSend(
            "/topic/room/" + chatRoom.getId(),
            messageDto
        );
    }

    @Transactional
    public void joinChatRoom(Long userId, Long roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Chat room not found"));

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        chatRoom.getParticipants().add(user);
        chatRoomRepository.save(chatRoom);

        // 방 참가 알림
        messagingTemplate.convertAndSend(
            "/topic/room/" + roomId + "/join",
            userId
        );
    }


}
