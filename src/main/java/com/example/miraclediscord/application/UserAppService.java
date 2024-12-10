package com.example.miraclediscord.application;

import com.example.miraclediscord.config.cookie.CookieManager;
import com.example.miraclediscord.config.oauth.OAuthToken;
import com.example.miraclediscord.dto.response.UserResponse;
import com.example.miraclediscord.model.entity.SocialProvider;
import com.example.miraclediscord.model.entity.User;
import com.example.miraclediscord.service.oauth.OAuthJwtService;
import com.example.miraclediscord.service.oauth.OAuthTokenService;
import com.example.miraclediscord.service.oauth.OAuthUserInfoService;
import com.example.miraclediscord.service.oauth.OAuthUserPersistenceService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAppService {

    private final OAuthTokenService oAuthTokenService;
    private final OAuthUserInfoService oAuthUserInfoService;
    private final OAuthUserPersistenceService oAuthUserPersistenceService;
    private final OAuthJwtService oAuthJwtService;
    private final CookieManager cookieManager;

    @Transactional
    public UserResponse.Login processOAuthLogin(String code, String state, SocialProvider socialProvider, HttpServletResponse response) {
        try {
            OAuthToken oauthToken = oAuthTokenService.requestToken(code, state, socialProvider);
            User oAuthUser = oAuthUserInfoService.getUserInfo(oauthToken.getAccess_token(), socialProvider);
            User savedOAuthUser = oAuthUserPersistenceService.saveOrUpdateOAuthUser(oAuthUser);
            String jwt = oAuthJwtService.createJwtToken(savedOAuthUser);
            response.addCookie(cookieManager.createJwtCookie(jwt));

            return UserResponse.Login.from(
                "success",
                socialProvider.name() + " 로그인 성공",
                oauthToken,
                savedOAuthUser
            );
        } catch (Exception e) {
            log.error("Failed to process {} login", socialProvider, e);
            throw new RuntimeException("Failed to process " + socialProvider + " login", e);
        }
    }

    public void logout(HttpServletResponse response) {
        cookieManager.removeJwtCookie(response);
    }

}
