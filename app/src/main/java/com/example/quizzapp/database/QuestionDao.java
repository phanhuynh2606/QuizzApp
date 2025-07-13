package com.example.quizzapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizzapp.models.Question;

import java.util.List;

@Dao
public interface QuestionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(Question question);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<Question> questions);

    @Update
    void updateQuestion(Question question);

    @Delete
    void deleteQuestion(Question question);

    @Query("DELETE FROM questions")
    void deleteAllQuestions();

    @Query("SELECT * FROM questions WHERE id = :id")
    Question getQuestionById(String id);

    @Query("SELECT * FROM questions WHERE quizId = :quizId ORDER BY orderIndex")
    List<Question> getQuestionsByQuizId(String quizId);

    @Query("SELECT * FROM questions ORDER BY orderIndex")
    List<Question> getAllQuestions();

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    void deleteQuestionsByQuizId(String quizId);

    @Query("SELECT COUNT(*) FROM questions WHERE quizId = :quizId")
    int getQuestionCountByQuizId(String quizId);

    @Query("SELECT * FROM questions WHERE quizId = :quizId AND orderIndex = :orderIndex LIMIT 1")
    Question getQuestionByQuizIdAndOrder(String quizId, int orderIndex);
}
