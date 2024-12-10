package com.example.miraclediscord.config.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

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

    public void removeJwtCookie(HttpServletResponse response) {
        // JWT 쿠키 만료
        Cookie expiredCookie = new Cookie(properties.getName(), null);
        expiredCookie.setMaxAge(0);  // 쿠키 즉시 만료
        expiredCookie.setPath(properties.getPath());
        response.addCookie(expiredCookie);

        // 보안 컨텍스트 클리어
        SecurityContextHolder.clearContext();
    }


}
