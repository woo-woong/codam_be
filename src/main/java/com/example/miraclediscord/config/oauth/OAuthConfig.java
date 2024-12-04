package com.example.miraclediscord.config.oauth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public class OAuthConfig {
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String tokenUrl;
    private final String userInfoUrl;
    private final HttpMethod userInfoMethod;
}

