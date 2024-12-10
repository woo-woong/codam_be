package com.example.miraclediscord.config.jwt;


import com.example.miraclediscord.dto.custom.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    static final SecretKey key =
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(
            "jwtpassword123jwtpassword123jwtpassword123jwtpassword123jwtpassword"
        ));

    // JWT 만들어주는 함수
    public static String createToken(@AuthenticationPrincipal CustomUser user) {

        String authorities = user.getAuthorities().stream()
            .map(authority -> authority.getAuthority()).collect(Collectors.joining(","));

        String jwt = Jwts.builder()
            .claim("username", user.getUsername())
            .claim("name", user.getName())
            .claim("authorities", authorities)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + 900_000)) // 유효기간 15분
            .signWith(key)
            .compact();
        return jwt;
    }

    // JWT 까주는 함수
    public static Claims extractToken(String token) {
        Claims claims = Jwts.parser().verifyWith(key).build()
            .parseSignedClaims(token).getPayload();
        return claims;
    }

}
