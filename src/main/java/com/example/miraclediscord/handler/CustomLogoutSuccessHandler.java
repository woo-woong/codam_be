package com.example.miraclediscord.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final ObjectMapper objectMapper;  // JSON 변환을 위한 매퍼

    @Override
    public void onLogoutSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication) throws IOException {

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, String> result = new HashMap<>();
        result.put("message", "로그아웃 되었습니다.");
        result.put("timestamp", LocalDateTime.now().toString());

        // 로그아웃한 사용자 정보가 있다면 포함
        if (authentication != null && authentication.getPrincipal() != null) {
            result.put("username", authentication.getName());
        }

        // JSON 응답 생성
        String jsonResult = objectMapper.writeValueAsString(result);
        response.getWriter().write(jsonResult);
        response.getWriter().flush();
    }
}