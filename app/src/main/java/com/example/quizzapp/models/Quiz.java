package com.example.quizzapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(tableName = "quizzes")
public class Quiz {
    @PrimaryKey
    @NonNull

    @SerializedName("_id")
    private String id;

    private String examCode;
    private String title;
    private String description;
    private String subjectCode;
    private int totalQuestions;
    private int timeLimit;
    private long duration;

    @SerializedName("examTypeId")
    private ExamType examType;
    private Date createdAt;
    private boolean isActive;

    public Quiz() {}

    @Ignore
    public Quiz( String examCode,String id, String title, String description, String subjectCode,
                int totalQuestions, int timeLimit, ExamType examType) {
        this.examCode = examCode;
        this.id = id;
        this.title = title;
        this.description = description;
        this.subjectCode = subjectCode;
        this.totalQuestions = totalQuestions;
        this.timeLimit = timeLimit;
        this.examType = examType;
        this.createdAt = new Date(); // tạo đối tượng Date hiện tại
        this.isActive = true;
    }

    public Quiz(boolean isActive, Date createdAt, ExamType examType, long duration, int timeLimit, int totalQuestions, String subjectCode, String description, String title, String examCode, @NonNull String id) {
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.examType = examType;
        this.duration = duration;
        this.timeLimit = timeLimit;
        this.totalQuestions = totalQuestions;
        this.subjectCode = subjectCode;
        this.description = description;
        this.title = title;
        this.examCode = examCode;
        this.id = id;
    }
    // Getters and setters

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getTimeLimit() { return timeLimit; }
    public void setTimeLimit(int timeLimit) { this.timeLimit = timeLimit; }

    public ExamType getExamType() { return examType; }
    public void setExamType(ExamType examType) { this.examType = examType; }

    public Date getCreatedAt() { return createdAt; } // sửa kiểu trả về thành Date
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}


