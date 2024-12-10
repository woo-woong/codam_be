package com.example.miraclediscord.service.oauth;

import com.example.miraclediscord.config.oauth.OAuthConfig;
import com.example.miraclediscord.config.oauth.OAuthConfigProperties;
import com.example.miraclediscord.config.oauth.OAuthToken;
import com.example.miraclediscord.model.entity.SocialProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuthTokenService {
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public OAuthToken requestToken(String code, String state, SocialProvider socialProvider) throws JsonProcessingException {
        OAuthConfig config = OAuthConfigProperties.OAUTH_CONFIGS.get(socialProvider);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_VALUE);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", config.getClientId());
        params.add("redirect_uri", config.getRedirectUri());
        params.add("code", code);

        if (socialProvider == SocialProvider.NAVER) {
            params.add("state", state);
        }
        if (!config.getClientSecret().isEmpty()) {
            params.add("client_secret", config.getClientSecret());
        }

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = restTemplate.exchange(
            config.getTokenUrl(),
            HttpMethod.POST,
            request,
            String.class
        );

        return objectMapper.readValue(response.getBody(), OAuthToken.class);
    }
}