package com.example.quizzapp.models.api;

import com.example.quizzapp.models.User;

/**
 * Response model cho API đăng nhập
 */
public class LoginResponse {
    private User user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String expiresIn;

    // Getters and setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getExpiresIn() { return expiresIn; }
    public void setExpiresIn(String expiresIn) { this.expiresIn = expiresIn; }
}
