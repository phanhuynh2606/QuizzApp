package com.example.quizzapp.models;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    @SerializedName("_id") // Ánh xạ từ _id trong JSON sang id trong model
    private String id = ""; // Khởi tạo mặc định để tránh null

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("role")
    private String role;

    @SerializedName("level")
    private String level;

    @SerializedName("isActive")
    private boolean isActive;

    @SerializedName("status")
    private String status;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("createdAt")
    private String createdAt; // Từ server (ISO string)

    @SerializedName("updatedAt")
    private String updatedAt; // Từ server (ISO string)

    @SerializedName("lastLogin")
    private String lastLogin; // Từ server (ISO string)

    // Statistics fields from API
    @SerializedName("totalQuizzes")
    private int totalQuizzes;

    @SerializedName("averageScore")
    private double averageScore;

    @SerializedName("totalStudyTime")
    private int totalStudyTime;

    // Arrays as JSON strings for Room database
    private String favoriteSubjects; // JSON string array
    private String fcmTokens; // JSON string array

    // Local fields for app functionality
    private String hashedPassword;
    private String salt;
    private long localCreatedAt;
    private boolean isLoggedIn;

    public User() {}

    @Ignore
    public User(String id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.localCreatedAt = System.currentTimeMillis();
        this.isLoggedIn = false;
        this.totalQuizzes = 0;
        this.averageScore = 0.0;
        this.totalStudyTime = 0;
        this.isActive = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id != null ? id : ""; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public int getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public double getAverageScore() { return averageScore; }
    public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

    public int getTotalStudyTime() { return totalStudyTime; }
    public void setTotalStudyTime(int totalStudyTime) { this.totalStudyTime = totalStudyTime; }

    public String getFavoriteSubjects() { return favoriteSubjects; }
    public void setFavoriteSubjects(String favoriteSubjects) { this.favoriteSubjects = favoriteSubjects; }

    public String getFcmTokens() { return fcmTokens; }
    public void setFcmTokens(String fcmTokens) { this.fcmTokens = fcmTokens; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public long getLocalCreatedAt() { return localCreatedAt; }
    public void setLocalCreatedAt(long localCreatedAt) { this.localCreatedAt = localCreatedAt; }

    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }
}
