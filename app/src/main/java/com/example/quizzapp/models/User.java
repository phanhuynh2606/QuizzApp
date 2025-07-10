package com.example.quizzapp.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

@Entity(tableName = "users")

public class User {
    @PrimaryKey
    @NonNull
    private String id;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private long createdAt;
    private boolean isLoggedIn;

    public User() {}

    @Ignore
    public User(String id,String username, String email, String password, String fullName) {
        this.id = id; // MongoDB ObjectId từ server
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.createdAt = System.currentTimeMillis();
        this.isLoggedIn = false;
    }
    public String getId() { return id; } // Đổi từ int sang String
    public void setId(String id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }
}
