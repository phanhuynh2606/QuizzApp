package com.example.quizzapp.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model để lưu đáp án của User cho từng câu hỏi
 * - Tự sinh trong app khi user trả lời câu hỏi
 * - Lưu vào SQLite để xem chi tiết offline
 * - Sync lên MongoDB server khi có mạng
 * - ID local auto-generate + serverId từ MongoDB
 */
@Entity(tableName = "user_answers")
public class UserAnswer {
    @PrimaryKey(autoGenerate = true) // ID local tự sinh
    private int id;

    // ID từ MongoDB khi sync thành công
    private String serverId; // ObjectId từ MongoDB

    private int quizHistoryId; // Local ID reference
    private String quizHistoryServerId; // MongoDB ObjectId reference
    private String questionId; // MongoDB ObjectId từ server
    private String questionText;
    private String optionA; // Thêm text đáp án A
    private String optionB; // Thêm text đáp án B
    private String optionC; // Thêm text đáp án C
    private String optionD; // Thêm text đáp án D
    private String userAnswer; // A, B, C, hoặc D
    private String correctAnswer; // A, B, C, hoặc D
    private boolean isCorrect;
    private int points;

    public UserAnswer() {}

    // Constructor khi user trả lời câu hỏi (không cần ID)
    public UserAnswer(int quizHistoryId, String questionId, String questionText,
                      String userAnswer, String correctAnswer, boolean isCorrect, int points) {
        this.quizHistoryId = quizHistoryId;
        this.questionId = questionId; // MongoDB ObjectId
        this.questionText = questionText;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.points = points;
    }

    // Constructor đầy đủ với tất cả đáp án
    public UserAnswer(int quizHistoryId, String questionId, String questionText,
                      String optionA, String optionB, String optionC, String optionD,
                      String userAnswer, String correctAnswer, boolean isCorrect, int points) {
        this.quizHistoryId = quizHistoryId;
        this.questionId = questionId;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.userAnswer = userAnswer;
        this.correctAnswer = correctAnswer;
        this.isCorrect = isCorrect;
        this.points = points;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getQuizHistoryId() { return quizHistoryId; }
    public void setQuizHistoryId(int quizHistoryId) { this.quizHistoryId = quizHistoryId; }

    public String getQuestionId() { return questionId; } // Đổi từ int sang String
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    // Getters and setters mới
    public String getServerId() { return serverId; }
    public void setServerId(String serverId) { this.serverId = serverId; }

    public String getQuizHistoryServerId() { return quizHistoryServerId; }
    public void setQuizHistoryServerId(String quizHistoryServerId) { this.quizHistoryServerId = quizHistoryServerId; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
}
