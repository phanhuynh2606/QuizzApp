package com.example.quizzapp.models.api;

public class ProfileResponse {
    private UserProfile user;

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    // Make UserProfile public so it can be accessed from DashboardActivity
    public static class UserProfile {
        private String _id;
        private String id;
        private String email;
        private String role;
        private String fullName;
        private String level;
        private boolean isActive;
        private String lastLogin;
        private String deviceType;
        private String createdAt;
        private String updatedAt;
        private String status;
        private int totalQuizzes;
        private double averageScore;
        private int totalStudyTime;
        private String[] favoriteSubjects;
        private String[] fcmTokens;

        // Getters and Setters
        public String get_id() { return _id; }
        public void set_id(String _id) { this._id = _id; }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }


        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public boolean isActive() { return isActive; }
        public void setActive(boolean active) { isActive = active; }

        public String getLastLogin() { return lastLogin; }
        public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }

        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public int getTotalQuizzes() { return totalQuizzes; }
        public void setTotalQuizzes(int totalQuizzes) { this.totalQuizzes = totalQuizzes; }

        public double getAverageScore() { return averageScore; }
        public void setAverageScore(double averageScore) { this.averageScore = averageScore; }

        public int getTotalStudyTime() { return totalStudyTime; }
        public void setTotalStudyTime(int totalStudyTime) { this.totalStudyTime = totalStudyTime; }

        public String[] getFavoriteSubjects() { return favoriteSubjects; }
        public void setFavoriteSubjects(String[] favoriteSubjects) { this.favoriteSubjects = favoriteSubjects; }

        public String[] getFcmTokens() { return fcmTokens; }
        public void setFcmTokens(String[] fcmTokens) { this.fcmTokens = fcmTokens; }
    }
}
