package com.example.miraclediscord.config.jwt;


import com.example.miraclediscord.model.entity.user.CustomUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.internal.Function;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${SECRET_KEY}")
    private String jwtSecret;

    // Access token expiration: 15 minutes
    private static final long ACCESS_TOKEN_EXPIRATION = 15 * 60 * 1000; // 15분

    // Refresh token expiration: 14 days
    private static final long REFRESH_TOKEN_EXPIRATION = 14 * 24 * 60 * 60 * 1000; // 14일

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
    }

    // Generate Access Token
    public String generateAccessToken(CustomUser user) {
        return generateToken(user, ACCESS_TOKEN_EXPIRATION);
    }

    // Generate Refresh Token
    public String generateRefreshToken(CustomUser user) {
        return generateToken(user, REFRESH_TOKEN_EXPIRATION);
    }

    private String generateToken(CustomUser user, long expirationTime) {
        String authorities = user.getAuthorities().stream()
            .map(authority -> authority.getAuthority())
            .collect(Collectors.joining(","));

        return Jwts.builder()
            .claim("email", user.getEmail())
            .claim("authorities", authorities)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(getSigningKey())
            .compact();
    }

    // Extract claims from token
    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    // Extract specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extract username from token
    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }


    // Check if token is expired
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // Validate token
    public boolean validateToken(String token, CustomUser userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
