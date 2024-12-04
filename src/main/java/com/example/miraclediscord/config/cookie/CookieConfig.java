package com.example.miraclediscord.config.cookie;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CookieConfig {

    @Bean
    public CookieProperties jwtCookieProperties() {
        return CookieProperties.builder()
            .name("jwt")
            .maxAge(900) // 15 minutes
            .httpOnly(true)
            .secure(true)
            .path("/")
            .build();
    }

    @Bean
    public CookieManager cookieManager(CookieProperties jwtCookieProperties) {
        return new CookieManager(jwtCookieProperties);
    }
}