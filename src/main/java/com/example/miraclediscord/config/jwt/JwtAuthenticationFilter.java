package com.example.miraclediscord.config.jwt;


import com.example.miraclediscord.dto.custom.CustomUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String[] excludePath = {"/login/**", "/signup/**", "/finance/**"};
        String path = request.getRequestURI();
        return pathMatcher.match(excludePath[0], path) ||
            pathMatcher.match(excludePath[1], path) ||
            pathMatcher.match(excludePath[2], path);

    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        System.out.println("JWT Filter 실행");

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtCookie = Arrays.stream(cookies)
            .filter(cookie -> "jwt".equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);

        if (jwtCookie == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = JwtProvider.extractToken(jwtCookie);
            System.out.println(claims.toString());
            String username = claims.get("username", String.class);

            // authorities 처리
            String[] authorities = claims.get("authorities", String.class).split(",");
            List<SimpleGrantedAuthority> grantedAuthorities = Arrays.stream(authorities)
                .map(SimpleGrantedAuthority::new)
                .toList();

            // CustomUser 객체 생성
            CustomUser customUser = new CustomUser(
                username,
                "none",  // password는 보안상 비워둠
                grantedAuthorities
            );

            // 추가 정보 설정
            if (claims.get("name") != null) customUser.setName(claims.get("name", String.class));

            // Authentication 객체 생성 및 설정
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(customUser, null, grantedAuthorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.out.println("JWT 처리 중 에러 발생: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
