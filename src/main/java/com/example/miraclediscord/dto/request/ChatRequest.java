package com.example.miraclediscord.dto.request;

import com.example.miraclediscord.model.entity.chat.ChatRoom;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatRequest {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRoom {
        private String name;
        private ChatRoom.ChatRoomType type;
        private List<Long> participantIds;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessage {
        private Long senderId;
        private Long roomId;
        private String content;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JoinRoom {
        private Long userId;
        private Long roomId;
    }
}