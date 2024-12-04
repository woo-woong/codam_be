package com.example.miraclediscord.controller;

import com.example.miraclediscord.application.UserAppService;
import com.example.miraclediscord.dto.response.UserResponse;
import com.example.miraclediscord.model.entity.SocialProvider;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserAppService userAppService;
    @GetMapping("/login/kakao")
    public ResponseEntity<UserResponse.Login> kakaoCallback(
        @RequestParam("code") String code,
        HttpServletResponse response
    ) {
        try {
            return ResponseEntity.ok(userAppService.processOAuthLogin(code, null, SocialProvider.KAKAO, response));
        } catch (Exception e) {
            log.error("Kakao login failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(UserResponse.Login.from("error", e.getMessage(), null, null));
        }
    }
}
