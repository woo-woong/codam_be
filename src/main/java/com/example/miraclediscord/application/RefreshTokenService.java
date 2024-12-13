package com.example.miraclediscord.application;

import com.example.miraclediscord.config.jwt.JwtProvider;
import com.example.miraclediscord.model.entity.RefreshToken;
import com.example.miraclediscord.model.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider; // JwtProvider 추가 권장

    @Transactional
    public void createOrUpdateRefreshToken(String email, String refreshToken) {
        // 기존 로직 유지
        refreshTokenRepository.deleteByEmail(email);
        RefreshToken token = new RefreshToken(email, refreshToken);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void validateRefreshToken(String refreshToken) {
        // 토큰 유효성 검증 로직 개선
        try {
            // JWT 토큰 파싱 및 만료 체크
            Claims claims = jwtProvider.extractAllClaims(refreshToken);
            String email = claims.get("email", String.class);

            // DB에서 토큰 검증
            RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰"));

            if (token.isExpired()) {
                // 만료된 토큰 삭제
                refreshTokenRepository.delete(token);
                throw new RuntimeException("만료된 리프레시 토큰");
            }
        } catch (ExpiredJwtException e) {
            // JWT 자체가 만료된 경우
            throw new RuntimeException("만료된 리프레시 토큰");
        } catch (JwtException e) {
            // 다른 JWT 관련 예외 처리
            throw new RuntimeException("유효하지 않은 리프레시 토큰");
        }
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken) {
        // RefreshToken으로 토큰 찾기 및 삭제
        refreshTokenRepository.findByToken(refreshToken)
            .ifPresent(refreshTokenRepository::delete);
    }
}