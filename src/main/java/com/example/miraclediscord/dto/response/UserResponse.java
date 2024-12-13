package com.example.miraclediscord.dto.response;


import com.example.miraclediscord.config.oauth.OAuthToken;
import com.example.miraclediscord.model.entity.user.User;
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
        private User oAuthUser;

        public static Login from(String status, String message, User oAuthUser) {
            return Login.builder()
                .status(status)
                .message(message)
                .oAuthUser(oAuthUser)
                .build();
        }
    }


}
