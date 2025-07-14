package com.example.quizzapp.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.quizzapp.database.Converters;

import java.util.HashMap;
import java.util.Map;

@Entity(tableName = "quiz_states")
@TypeConverters({Converters.class})
public class QuizState {
    @PrimaryKey
    @NonNull
    private String quizId;
    private String subjectCode;
    private String quizTitle;
    private int currentQuestionIndex;
    private long timeLeftInMillis;
    private Map<Integer, String> userAnswers;
    private long timestamp;

    public QuizState() {
        this.userAnswers = new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    public QuizState(String quizId, String subjectCode, String quizTitle,
                     int currentQuestionIndex, long timeLeftInMillis,
                     Map<Integer, String> userAnswers) {
        this.quizId = quizId;
        this.subjectCode = subjectCode;
        this.quizTitle = quizTitle;
        this.currentQuestionIndex = currentQuestionIndex;
        this.timeLeftInMillis = timeLeftInMillis;
        this.userAnswers = userAnswers != null ? userAnswers : new HashMap<>();
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getCurrentQuestionIndex() {
        return currentQuestionIndex;
    }

    public void setCurrentQuestionIndex(int currentQuestionIndex) {
        this.currentQuestionIndex = currentQuestionIndex;
    }

    public long getTimeLeftInMillis() {
        return timeLeftInMillis;
    }

    public void setTimeLeftInMillis(long timeLeftInMillis) {
        this.timeLeftInMillis = timeLeftInMillis;
    }

    public Map<Integer, String> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(Map<Integer, String> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Utility methods
    public boolean isExpired() {
        // Kiểm tra nếu quiz state đã quá 24 giờ (có thể điều chỉnh)
        long maxAge = 24 * 60 * 60 * 1000; // 24 giờ
        return System.currentTimeMillis() - timestamp > maxAge;
    }

    public boolean hasTimeLeft() {
        return timeLeftInMillis > 0;
    }

    public int getAnsweredCount() {
        return userAnswers != null ? userAnswers.size() : 0;
    }

    public double getCompletionPercentage(int totalQuestions) {
        if (totalQuestions == 0) return 0;
        return (double) getAnsweredCount() / totalQuestions * 100;
    }

    public String getFormattedTimeLeft() {
        if (timeLeftInMillis <= 0) return "00:00";

        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    @Override
    public String toString() {
        return "QuizState{" +
                "quizId='" + quizId + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", quizTitle='" + quizTitle + '\'' +
                ", currentQuestionIndex=" + currentQuestionIndex +
                ", timeLeftInMillis=" + timeLeftInMillis +
                ", answeredCount=" + getAnsweredCount() +
                ", timestamp=" + timestamp +
                '}';
    }
}