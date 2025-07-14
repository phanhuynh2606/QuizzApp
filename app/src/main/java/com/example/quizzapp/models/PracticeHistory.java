package com.example.quizzapp.models;

import java.util.List;

public class PracticeHistory {
    private String examId;
    private String subjectCode;
    private String examTypeCode;
    private String userSessionId;
    private String startTime;
    private String endTime;
    private int totalQuestions;
    private int correctAnswers;
    private double score;
    private long timeSpent;
    private List<AnswerRequest> answers;
    private boolean isCompleted;
    public PracticeHistory(String examId, String subjectCode, String examTypeCode,
                           String userSessionId, String startTime, String endTime, int totalQuestions,
                           int correctAnswers, double score, long timeSpent, List<AnswerRequest> answers,
                           boolean isCompleted) {
        this.examId = examId;
        this.subjectCode = subjectCode;
        this.examTypeCode = examTypeCode;
        this.userSessionId = userSessionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.timeSpent = timeSpent;
        this.answers = answers;
        this.isCompleted = isCompleted;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public List<AnswerRequest> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerRequest> answers) {
        this.answers = answers;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public void setTimeSpent(long timeSpent) {
        this.timeSpent = timeSpent;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public String getExamTypeCode() {
        return examTypeCode;
    }

    public void setExamTypeCode(String examTypeCode) {
        this.examTypeCode = examTypeCode;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }
}
