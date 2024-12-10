package com.example.miraclediscord.config.cookie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CookieProperties {
    private String name;
    private int maxAge;
    private boolean httpOnly;
    private boolean secure;
    private String path;
}