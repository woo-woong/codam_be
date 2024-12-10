package com.example.miraclediscord.service.oauth;

import com.example.miraclediscord.config.jwt.JwtProvider;
import com.example.miraclediscord.dto.custom.CustomUser;
import com.example.miraclediscord.model.entity.User;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class OAuthJwtService {
    public String createJwtToken(User oAuthUser) {
        List<GrantedAuthority> authorities = List.of(
            new SimpleGrantedAuthority(oAuthUser.getSocialProvider().name() + "_USER")
        );
        CustomUser customUser = new CustomUser(
            oAuthUser.getEmail(),
            "",
            authorities
        );
        return JwtProvider.createToken(customUser);
    }
}