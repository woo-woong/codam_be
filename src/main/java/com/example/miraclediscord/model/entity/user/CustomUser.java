package com.example.miraclediscord.model.entity.user;

import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
@Setter
public class CustomUser extends User {
    private final String email;

    public CustomUser(String email, String password, Collection<? extends GrantedAuthority> authorities) {
        super(email, password, authorities);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getUsername() {
        return email; // username 대신 email 반환
    }
}