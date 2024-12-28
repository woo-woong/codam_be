package com.example.miraclediscord.model.repository;

import com.example.miraclediscord.model.entity.chat.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoom_IdOrderByTimestampDesc(Long roomId);
}
