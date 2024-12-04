package com.example.miraclediscord.config.cookie;

import jakarta.servlet.http.Cookie;

public class CookieManager {
    private final CookieProperties properties;

    public CookieManager(CookieProperties properties) {
        this.properties = properties;
    }

    public Cookie createJwtCookie(String token) {
        Cookie cookie = new Cookie(properties.getName(), token);
        cookie.setMaxAge(properties.getMaxAge());
        cookie.setHttpOnly(properties.isHttpOnly());
        cookie.setSecure(properties.isSecure());
        cookie.setPath(properties.getPath());
        return cookie;
    }
}
