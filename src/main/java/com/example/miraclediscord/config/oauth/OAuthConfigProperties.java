package com.example.miraclediscord.config.oauth;


import com.example.miraclediscord.model.entity.SocialProvider;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

public class OAuthConfigProperties {
    @Value("${KAKAO_CLIENT_ID}")
    private String kakaoClientId;


    public static final Map<SocialProvider, OAuthConfig> OAUTH_CONFIGS = Map.of(
        SocialProvider.GOOGLE, new OAuthConfig(
            "${KAKAO_CLIENT_ID}",
            "clientSecret",
            "http://localhost:3000/login/oauth2/code/google",
            "https://oauth2.googleapis.com/token",
            "https://www.googleapis.com/oauth2/v2/userinfo",
            HttpMethod.GET
        ),
        SocialProvider.KAKAO, new OAuthConfig(
            "b45153d1f2c4253b2963ed5d569cbb7c",
            "",
            "http://localhost:3000/login/oauth2/code/kakao",
            "https://kauth.kakao.com/oauth/token",
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST
        ),
        SocialProvider.NAVER, new OAuthConfig(
            "clientID",
            "clientSecret",
            "http://localhost:3000/login/oauth2/code/naver",
            "https://nid.naver.com/oauth2.0/token",
            "https://openapi.naver.com/v1/nid/me",
            HttpMethod.GET
        )
    );

}
