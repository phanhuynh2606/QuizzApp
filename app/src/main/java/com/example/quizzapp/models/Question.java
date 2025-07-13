package com.example.quizzapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

/**
 * Model để chứa dữ liệu Question từ API
 * - Lấy từ server qua API /quizzes/{id}/questions
 * - Lưu vào SQLite để làm bài offline
 * - ID từ MongoDB ObjectId (String)
 */
@Entity(tableName = "questions")
public class Question implements Serializable {
    @PrimaryKey // MongoDB ObjectId từ server API
    private String id; // "_id": "64f8a1b2c3d4e5f6789012ab"
    private String quizId; // Foreign key tới Quiz (MongoDB ObjectId)
    private String questionText;
    private List<Option> options;
    private CorrectAnswer correctAnswer;
    private String explanation;
    private int points;
    private int orderIndex; // Thứ tự hiển thị câu hỏi (1, 2, 3, ...)

    public Question() {}

    // Constructor cho dữ liệu từ API
    public Question(String id, String quizId, String questionText, List<Option> optionText,
                    CorrectAnswer correctAnswer, String explanation, int points, int orderIndex) {
        this.id = id; // MongoDB ObjectId từ server
        this.quizId = quizId; // MongoDB ObjectId của Quiz
        this.questionText = questionText;
        this.options = optionText;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.points = points;
        this.orderIndex = orderIndex; // Thứ tự câu hỏi
    }

    // Getters and setters
    public String getId() { return id; } // Đổi từ int sang String
    public void setId(String id) { this.id = id; }

    public String getQuizId() { return quizId; } // Đổi từ int sang String
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public String getCorrectAnswerLetter() {
        return correctAnswer != null ? correctAnswer.getAnswer() : null;
    }

    public void setCorrectAnswer(CorrectAnswer correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
