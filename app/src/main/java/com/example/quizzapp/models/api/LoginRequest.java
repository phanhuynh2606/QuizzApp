package com.example.quizzapp.models.api;

/**
 * Request model cho API đăng nhập
 */
public class LoginRequest {
    private String email; // Thay đổi từ loginInput thành email
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
