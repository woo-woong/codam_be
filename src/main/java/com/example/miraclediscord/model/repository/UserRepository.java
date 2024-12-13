package com.example.miraclediscord.model.repository;

import com.example.miraclediscord.model.entity.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsBySocialId(String socialId);
    Optional<User> findBySocialId(String socialId);
    Optional<User> findByEmail(String email);

}
