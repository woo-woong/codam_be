package com.example.miraclediscord.dto.response;

import lombok.Builder;
import lombok.Data;

public class TokenResponse {
    // 정적 내부 클래스로 Detail 정의
    @Data
    @Builder
    public static class Detail {
        private final String accessToken;
        private final String refreshToken;

        public Detail(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }
    }
}