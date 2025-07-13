package com.example.quizzapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizzapp.models.Quiz;

import java.util.List;

@Dao
public interface QuizDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuiz(Quiz quiz);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuizzes(List<Quiz> quizzes);

    @Update
    void updateQuiz(Quiz quiz);

    @Delete
    void deleteQuiz(Quiz quiz);

    @Query("DELETE FROM quizzes")
    void deleteAllQuizzes();

    @Query("SELECT * FROM quizzes WHERE id = :id")
    Quiz getQuizById(String id); // Đổi từ int sang String

    @Query("SELECT * FROM quizzes WHERE isActive = 1 ORDER BY createdAt DESC")
    List<Quiz> getAllActiveQuizzes();

    @Query("SELECT * FROM quizzes WHERE subjectCode = :subject AND isActive = 1 ORDER BY createdAt DESC")
    List<Quiz> getQuizzesBySubject(String subject);

    @Query("SELECT DISTINCT subjectCode FROM quizzes WHERE isActive = 1")
    List<String> getAllSubjects();

    @Query("SELECT * FROM quizzes ORDER BY createdAt DESC")
    List<Quiz> getAllQuizzes();
}
