package com.example.quizzapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model để chứa dữ liệu Question từ API
 * - Lấy từ server qua API /quizzes/{id}/questions
 * - Lưu vào SQLite để làm bài offline
 * - ID từ MongoDB ObjectId (String)
 */
@Entity(tableName = "questions")
public class Question {
    @PrimaryKey // MongoDB ObjectId từ server API
    private String id; // "_id": "64f8a1b2c3d4e5f6789012ab"
    private String quizId; // Foreign key tới Quiz (MongoDB ObjectId)
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String correctAnswer; // A, B, C, hoặc D
    private String explanation;
    private int points;
    private int orderIndex; // Thứ tự hiển thị câu hỏi (1, 2, 3, ...)

    public Question() {}

    // Constructor cho dữ liệu từ API
    public Question(String id, String quizId, String questionText, String optionA,
                    String optionB, String optionC, String optionD,
                    String correctAnswer, String explanation, int points, int orderIndex) {
        this.id = id; // MongoDB ObjectId từ server
        this.quizId = quizId; // MongoDB ObjectId của Quiz
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
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

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }
}
