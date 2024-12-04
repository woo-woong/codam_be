package com.example.miraclediscord.config.oauth;


import lombok.Data;

@Data //(2)
public class OAuthToken { //(1)
    private String access_token;
    private String token_type;
    private String refresh_token;
    private int expires_in;
    private String scope;
    private int refresh_token_expires_in;
    private String id_token;

}
