package com.example.miraclediscord.application;

import com.example.miraclediscord.config.cookie.CookieManager;
import com.example.miraclediscord.config.jwt.JwtProvider;
import com.example.miraclediscord.config.oauth.OAuthToken;
import com.example.miraclediscord.dto.response.TokenResponse;
import com.example.miraclediscord.dto.response.UserResponse;
import com.example.miraclediscord.model.entity.SocialProvider;
import com.example.miraclediscord.model.entity.user.CustomUser;
import com.example.miraclediscord.model.entity.user.User;
import com.example.miraclediscord.model.repository.UserRepository;
import com.example.miraclediscord.service.oauth.OAuthJwtService;
import com.example.miraclediscord.service.oauth.OAuthTokenService;
import com.example.miraclediscord.service.oauth.OAuthUserInfoService;
import com.example.miraclediscord.service.oauth.OAuthUserPersistenceService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppService {

    private final OAuthTokenService oAuthTokenService;
    private final OAuthUserInfoService oAuthUserInfoService;
    private final OAuthUserPersistenceService oAuthUserPersistenceService;
    private final OAuthJwtService oAuthJwtService;
    private final CookieManager cookieManager;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public UserResponse.Login processOAuthLogin(String code, String state, SocialProvider socialProvider, HttpServletResponse response) {
        try {
            OAuthToken oauthToken = oAuthTokenService.requestToken(code, state, socialProvider);
            User oAuthUser = oAuthUserInfoService.getUserInfo(oauthToken.getAccess_token(), socialProvider);
            User savedOAuthUser = oAuthUserPersistenceService.saveOrUpdateOAuthUser(oAuthUser);

            // 토큰 생성 및 쿠키 추가 로직 수정
            TokenResponse.Detail tokenResponse = oAuthJwtService.createTokens(savedOAuthUser);

            response.addCookie(cookieManager.createAccessTokenCookie(tokenResponse.getAccessToken()));
            response.addCookie(cookieManager.createRefreshTokenCookie(tokenResponse.getRefreshToken()));

            return UserResponse.Login.from(
                "success",
                socialProvider.name() + " 로그인 성공",
                savedOAuthUser
            );
        } catch (Exception e) {
            log.error("Failed to process {} login", socialProvider, e);
            throw new RuntimeException("Failed to process " + socialProvider + " login", e);
        }
    }

    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // 리프레시 토큰 검증
        refreshTokenService.validateRefreshToken(refreshToken);

        // 토큰에서 사용자 정보 추출
        Claims claims = jwtProvider.extractAllClaims(refreshToken);
        String email = claims.get("email", String.class);

        // 사용자 조회
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없음"));

        // 새 액세스 토큰 생성
        CustomUser customUser = new CustomUser(
            user.getEmail(),
            "N/A",
            List.of(
                new SimpleGrantedAuthority(user.getSocialProvider().name() + "_USER")
            )
        );

        return jwtProvider.generateAccessToken(customUser);
    }



}
