package com.example.miraclediscord.controller;

import com.example.miraclediscord.application.UserAppService;
import com.example.miraclediscord.dto.response.UserResponse;
import com.example.miraclediscord.model.entity.SocialProvider;
import com.example.miraclediscord.model.entity.user.CustomUser;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
                .body(UserResponse.Login.from("error", e.getMessage(), null));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal CustomUser user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(user.getUsername());
        System.out.println(user.getEmail());
        System.out.println(user.getAuthorities());
        if (authentication != null && authentication.getPrincipal() instanceof CustomUser) {
            CustomUser customUser = (CustomUser) authentication.getPrincipal();
            return ResponseEntity.ok(customUser);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }



}
