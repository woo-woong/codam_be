package com.example.miraclediscord.config.jwt;

import com.example.miraclediscord.application.UserAppService;
import com.example.miraclediscord.config.cookie.CookieManager;
import com.example.miraclediscord.model.entity.user.CustomUser;
import com.example.miraclediscord.model.repository.RefreshTokenRepository;
import com.example.miraclediscord.model.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieManager cookieManager;
    private final UserAppService userAppService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            clearAuthenticationWithoutDB(response);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = extractToken(cookies, "access_token");
        String refreshToken = extractToken(cookies, "refresh_token");

        // refresh token이 없는 경우 로그아웃 처리
        if (refreshToken == null) {
            clearAuthenticationWithoutDB(response);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // refresh token으로 사용자 찾기
            Claims refreshClaims = jwtProvider.extractAllClaims(refreshToken);
            String userEmail = refreshClaims.get("email", String.class);

            if (accessToken == null) {
                String newAccessToken = userAppService.refreshAccessToken(refreshToken);
                response.addCookie(cookieManager.createAccessTokenCookie(newAccessToken));
                accessToken = newAccessToken;
            }

            Claims claims = jwtProvider.extractAllClaims(accessToken);

            if (jwtProvider.isTokenExpired(accessToken)) {
                clearAuthenticationWithDB(response, refreshToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String email = claims.get("email", String.class);
            String authoritiesStr = claims.get("authorities", String.class);

            List<GrantedAuthority> authorities = Arrays.stream(authoritiesStr.split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

            CustomUser userDetails = new CustomUser(email, "", authorities);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            if (refreshToken != null) {
                clearAuthenticationWithDB(response, refreshToken);
            } else {
                clearAuthenticationWithoutDB(response);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void clearAuthenticationWithDB(HttpServletResponse response, String refreshToken) {
        SecurityContextHolder.clearContext();

        try {
            // RefreshTokenRepository에서 토큰 삭제
            refreshTokenRepository.deleteByToken(refreshToken);
        } catch (Exception e) {
            log.error("Failed to delete refresh token from database", e);
        }

        response.addCookie(cookieManager.createExpiredAccessTokenCookie());
        response.addCookie(cookieManager.createExpiredRefreshTokenCookie());
    }

    private void clearAuthenticationWithoutDB(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addCookie(cookieManager.createExpiredAccessTokenCookie());
        response.addCookie(cookieManager.createExpiredRefreshTokenCookie());
    }

    private String extractToken(Cookie[] cookies, String tokenName) {
        return Arrays.stream(cookies)
            .filter(cookie -> tokenName.equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }
}