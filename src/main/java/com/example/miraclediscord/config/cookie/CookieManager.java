package com.example.miraclediscord.config.cookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CookieManager {
    private final CookieProperties accessTokenCookieProperties;
    private final CookieProperties refreshTokenCookieProperties;

    public CookieManager(
        CookieProperties accessTokenCookieProperties,
        CookieProperties refreshTokenCookieProperties
    ) {
        this.accessTokenCookieProperties = accessTokenCookieProperties;
        this.refreshTokenCookieProperties = refreshTokenCookieProperties;
    }

    public Cookie createAccessTokenCookie(String token) {
        return createCookie(
            accessTokenCookieProperties.getName(),
            token,
            accessTokenCookieProperties.getMaxAge()
        );
    }

    public Cookie createRefreshTokenCookie(String token) {
        return createCookie(
            refreshTokenCookieProperties.getName(),
            token,
            refreshTokenCookieProperties.getMaxAge()
        );
    }

    public Cookie createExpiredAccessTokenCookie() {
        Cookie cookie = new Cookie(accessTokenCookieProperties.getName(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public Cookie createExpiredRefreshTokenCookie() {
        Cookie cookie = new Cookie(refreshTokenCookieProperties.getName(), null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        return cookie;
    }

    public void removeAccessTokenCookie(HttpServletResponse response) {
        removeCookie(response, accessTokenCookieProperties.getName());
    }

    public void removeRefreshTokenCookie(HttpServletResponse response) {
        removeCookie(response, refreshTokenCookieProperties.getName());
    }

    private void removeCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public String getCookieValue(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}