package com.example.miraclediscord.dto.response;


import com.example.miraclediscord.config.oauth.OAuthToken;
import com.example.miraclediscord.model.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class UserResponse {

    @Data
    @Builder
    public static class Detail {

        private String email;

        public static Detail from(User user) {
            return Detail.builder()
                .email(user.getEmail())
                .build();
        }
    }

    @Data
    @Builder
    public static class Login{
        private String status;
        private String message;
        private OAuthToken token;
        private User oAuthUser;

        public static Login from(String status, String message, OAuthToken token, User oAuthUser) {
            return Login.builder()
                .status(status)
                .message(message)
                .token(token)
                .oAuthUser(oAuthUser)
                .build();
        }
    }


}
