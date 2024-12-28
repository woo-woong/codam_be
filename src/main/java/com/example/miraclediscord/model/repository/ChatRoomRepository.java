package com.example.miraclediscord.model.repository;

import com.example.miraclediscord.model.entity.chat.ChatRoom;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByType(ChatRoom.ChatRoomType type);
    List<ChatRoom> findByParticipants_Id(Long userId);
}