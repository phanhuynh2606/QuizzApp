package com.example.quizzapp.models.api;

/**
 * Request model cho API đăng nhập
 */
public class LoginRequest {
    private String loginInput; // Có thể là email hoặc username
    private String password;

    public LoginRequest(String loginInput, String password) {
        this.loginInput = loginInput;
        this.password = password;
    }

    // Getters and setters
    public String getLoginInput() { return loginInput; }
    public void setLoginInput(String loginInput) { this.loginInput = loginInput; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
