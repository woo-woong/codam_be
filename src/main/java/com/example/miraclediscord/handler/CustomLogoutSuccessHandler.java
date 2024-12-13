package com.example.miraclediscord.handler;

import com.example.miraclediscord.application.RefreshTokenService;
import com.example.miraclediscord.config.cookie.CookieManager;
import com.example.miraclediscord.model.entity.user.CustomUser;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final RefreshTokenService refreshTokenService;
    private final CookieManager cookieManager;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        System.out.println("여기는 커스텀 로그아웃");

        // 쿠키 또는 헤더에서 RefreshToken 가져오기
        String refreshToken = cookieManager.getCookieValue(request, "refresh_token"); // 쿠키에서 가져오기 예시

        if (refreshToken != null) {
            try {
                // RefreshToken DB에서 제거
                refreshTokenService.deleteRefreshToken(refreshToken);
            } catch (Exception e) {
                log.error("Failed to delete refresh token from database", e);
            }
        } else {
            log.warn("No refresh token found in request");
        }

        // 쿠키 삭제
        response.addCookie(cookieManager.createExpiredAccessTokenCookie());
        response.addCookie(cookieManager.createExpiredRefreshTokenCookie());

        // 로그아웃 성공 응답
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }



}
