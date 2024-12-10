package com.example.miraclediscord.service.oauth;

import com.example.miraclediscord.config.oauth.OAuthConfig;
import com.example.miraclediscord.config.oauth.OAuthConfigProperties;
import com.example.miraclediscord.model.entity.SocialProvider;
import com.example.miraclediscord.model.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthUserInfoService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public User getUserInfo(String accessToken, SocialProvider socialProvider) {
        OAuthConfig config = OAuthConfigProperties.OAUTH_CONFIGS.get(socialProvider);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
            config.getUserInfoUrl(),
            config.getUserInfoMethod(),
            request,
            String.class
        );

        try {
            Map<String, Object> userInfo = objectMapper.readValue(response.getBody(), Map.class);
            return extractUserInfo(userInfo, socialProvider);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse {} user info", socialProvider, e);
            throw new RuntimeException("Failed to parse " + socialProvider + " user info", e);
        }
    }

    private User extractUserInfo(Map<String, Object> userInfo, SocialProvider socialProvider) {
        String providerId;
        String name;
        String email;
        String profileImg;

        switch (socialProvider) {
            case GOOGLE:
                providerId = (String) userInfo.get("id");
                name = (String) userInfo.get("name");
                email = (String) userInfo.get("email");
                profileImg = (String) userInfo.get("picture");
                break;

            case KAKAO:
                providerId = String.valueOf(userInfo.get("id"));
                Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                name = (String) profile.get("nickname");
                email = (String) kakaoAccount.get("email");
                profileImg = (String) profile.get("profile_image_url");
                break;

            case NAVER:
                Map<String, Object> response = (Map<String, Object>) userInfo.get("response");
                providerId = (String) response.get("id");
                name = (String) response.get("name");
                email = (String) response.get("email");
                profileImg = (String) response.get("profile_image");
                break;

            default:
                throw new IllegalArgumentException("Unsupported provider: " + socialProvider);
        }

        return User.builder()
            .socialId(providerId)
            .name(name)
            .email(email)
            .profileImg(profileImg)
            .socialProvider(socialProvider)
            .build();
    }
}
