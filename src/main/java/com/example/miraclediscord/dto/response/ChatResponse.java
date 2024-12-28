package com.example.miraclediscord.dto.response;

import com.example.miraclediscord.model.entity.chat.ChatRoom;
import com.example.miraclediscord.model.entity.chat.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ChatResponse {
    @Data
    @Builder
    public static class Detail {
        private Long id;
        private String name;
        private ChatRoom.ChatRoomType type;
        private List<UserResponse.Simple> participants;

        public static Detail from(ChatRoom chatRoom) {
            return Detail.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .type(chatRoom.getType())
                .participants(chatRoom.getParticipants().stream()
                    .map(UserResponse.Simple::from)
                    .collect(Collectors.toList()))
                .build();
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageDetail {
        private Long id;
        private UserResponse.Simple sender;
        private String content;
        private LocalDateTime timestamp;
        private Long roomId;

        public static MessageDetail from(ChatMessage message) {
            return new MessageDetail(
                message.getId(),
                UserResponse.Simple.from(message.getSender()),
                message.getContent(),
                message.getTimestamp(),
                message.getChatRoom().getId()
            );
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomList {
        private List<Detail> rooms;

        public static RoomList from(List<ChatRoom> chatRooms) {
            return new RoomList(
                chatRooms.stream()
                    .map(Detail::from)
                    .collect(Collectors.toList())
            );
        }
    }
}
