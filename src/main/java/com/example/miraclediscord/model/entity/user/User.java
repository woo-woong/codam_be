package com.example.miraclediscord.model.entity.user;

import com.example.miraclediscord.model.entity.SocialProvider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 소셜 로그인 제공자 열거형
    @Enumerated(EnumType.STRING)
    @Column
    private SocialProvider socialProvider;

    // 각 소셜 플랫폼의 고유 식별자
    @Column(unique = true)
    private String socialId;

    @Column
    private String name;

    @Column(length = 500)
    private String profileImg;

    @Column(unique = true)
    private String email;


    @Column
    private Boolean isActive = true;

    public void updateActiveStatus(boolean active) {
        this.isActive = active;
    }
}