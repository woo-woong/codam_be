package com.example.miraclediscord.service.oauth;

import com.example.miraclediscord.model.entity.User;
import com.example.miraclediscord.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthUserPersistenceService {
    private final UserRepository userRepository;

    @Transactional
    public User saveOrUpdateOAuthUser(User oAuthUser) {
        return userRepository.findBySocialId(oAuthUser.getSocialId())
            .orElseGet(() -> userRepository.save(
                User.builder()
                    .email(oAuthUser.getEmail())
                    .name(oAuthUser.getName())
                    .socialId(oAuthUser.getSocialId())
                    .profileImg(oAuthUser.getProfileImg())
                    .socialProvider(oAuthUser.getSocialProvider())
                    .isActive(true)
                    .build()
            ));
    }
}