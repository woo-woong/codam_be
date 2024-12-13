package com.example.miraclediscord.config.cookie;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    @Qualifier("accessTokenCookieProperties")
    public CookieProperties accessTokenCookieProperties() {
        return CookieProperties.builder()
            .name("access_token")
            .maxAge(900) // 15 minutes
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();
    }

    @Bean
    @Qualifier("refreshTokenCookieProperties")
    public CookieProperties refreshTokenCookieProperties() {
        return CookieProperties.builder()
            .name("refresh_token")
            .maxAge(14 * 24 * 60 * 60) // 14 days
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();
    }

    @Bean
    public CookieManager cookieManager(
        @Qualifier("accessTokenCookieProperties") CookieProperties accessTokenCookieProperties,
        @Qualifier("refreshTokenCookieProperties") CookieProperties refreshTokenCookieProperties
    ) {
        return new CookieManager(
            accessTokenCookieProperties,
            refreshTokenCookieProperties
        );
    }
}