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

    @SerializedName("email")
    private String email;

    @SerializedName("fullName")
    private String fullName;

    @SerializedName("role")
    private String role;

    @SerializedName("deviceType")
    private String deviceType;

    @SerializedName("createdAt")
    private String serverCreatedAt; // Từ server (ISO string)

    @SerializedName("lastLogin")
    private String lastLogin; // Từ server (ISO string)

    // Lưu password đã hash và salt để bảo mật
    private String hashedPassword;
    private String salt;

    private long localCreatedAt; // Đổi tên để tránh conflict với serverCreatedAt
    private boolean isLoggedIn;

    public User() {}

    @Ignore
    public User(String id, String email, String fullName) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.localCreatedAt = System.currentTimeMillis();
        this.isLoggedIn = false;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id != null ? id : ""; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getServerCreatedAt() { return serverCreatedAt; }
    public void setServerCreatedAt(String serverCreatedAt) { this.serverCreatedAt = serverCreatedAt; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public long getLocalCreatedAt() { return localCreatedAt; }
    public void setLocalCreatedAt(long localCreatedAt) { this.localCreatedAt = localCreatedAt; }

    public boolean isLoggedIn() { return isLoggedIn; }
    public void setLoggedIn(boolean loggedIn) { isLoggedIn = loggedIn; }

    // Phương thức để tương thích với code cũ
    public String getUsername() { return email; }
    public void setUsername(String username) { this.email = username; }

    // Deprecated - không nên sử dụng trực tiếp
    @Deprecated
    public String getPassword() { return hashedPassword; }

    @Deprecated
    public void setPassword(String password) {
        // Cảnh báo: phương thức này deprecated, nên sử dụng setHashedPassword
        this.hashedPassword = password;
    }

    // Phương thức tương thích ngược cho createdAt
    public long getCreatedAt() { return localCreatedAt; }
    public void setCreatedAt(long createdAt) { this.localCreatedAt = createdAt; }
}
