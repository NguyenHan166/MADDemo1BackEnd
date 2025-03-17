package com.nguyenhan.maddemo1.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private OAuth2User oauth2User;
    private User user;
    private String token;

    public CustomOAuth2User(OAuth2User oauth2User, User user, String token) {
        this.oauth2User = oauth2User;
        this.user = user;
        this.token = token;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return user.getFullname();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }
}

