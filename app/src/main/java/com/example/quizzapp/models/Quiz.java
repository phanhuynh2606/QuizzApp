package com.example.quizzapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model để chứa dữ liệu Quiz từ API
 * - Lấy từ server qua API /quizzes
 * - Lưu vào SQLite để xem offline
 * - ID từ MongoDB ObjectId (String)
 */
@Entity(tableName = "quizzes")
public class Quiz {
    @PrimaryKey // MongoDB ObjectId từ server API
    private String id; // "_id": "64f8a1b2c3d4e5f6789012ab"
    private String title;
    private String description;
    private String subject;
    private int totalQuestions;
    private int timeLimit; // in minutes
    private String difficulty;
    private long createdAt;
    private boolean isActive;

    public Quiz() {}

    // Constructor cho dữ liệu từ API
    public Quiz(String id, String title, String description, String subject,
                int totalQuestions, int timeLimit, String difficulty) {
        this.id = id; // MongoDB ObjectId từ server
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.timeLimit = timeLimit;
        this.difficulty = difficulty;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters and setters
    public String getId() { return id; } // Đổi từ int sang String
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}
