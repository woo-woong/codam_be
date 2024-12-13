package com.example.miraclediscord.service.oauth;

import com.example.miraclediscord.application.RefreshTokenService;
import com.example.miraclediscord.config.jwt.JwtProvider;
import com.example.miraclediscord.dto.response.TokenResponse;
import com.example.miraclediscord.model.entity.user.CustomUser;
import com.example.miraclediscord.model.entity.user.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OAuthJwtService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse.Detail createTokens(User oAuthUser) {
        // 권한 생성
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(oAuthUser.getSocialProvider().name() + "_USER")
        );

        // CustomUser 생성
        CustomUser customUser = new CustomUser(
            oAuthUser.getEmail(),
            "",
            authorities
        );

        // Access Token 생성
        String accessToken = jwtProvider.generateAccessToken(customUser);

        // Refresh Token 생성
        String refreshToken = jwtProvider.generateRefreshToken(customUser);

        // Refresh Token을 데이터베이스에 저장
        refreshTokenService.createOrUpdateRefreshToken(
            oAuthUser.getEmail(),
            refreshToken
        );

        // 토큰 응답 객체 반환
        return new TokenResponse.Detail(accessToken, refreshToken);
    }


}