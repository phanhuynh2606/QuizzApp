package com.example.quizzapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizzapp.models.UserAnswer;

import java.util.List;

@Dao
public interface UserAnswerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserAnswer(UserAnswer userAnswer);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertUserAnswers(List<UserAnswer> userAnswers);

    @Update
    void updateUserAnswer(UserAnswer userAnswer);

    @Delete
    void deleteUserAnswer(UserAnswer userAnswer);

    @Query("DELETE FROM user_answers")
    void deleteAllUserAnswers();

    @Query("SELECT * FROM user_answers WHERE id = :id")
    UserAnswer getUserAnswerById(int id);

    @Query("SELECT * FROM user_answers WHERE quizHistoryId = :quizHistoryId ORDER BY id")
    List<UserAnswer> getUserAnswersByHistoryId(int quizHistoryId);

    @Query("SELECT * FROM user_answers WHERE questionId = :questionId")
    List<UserAnswer> getUserAnswersByQuestionId(String questionId);

    @Query("SELECT * FROM user_answers ORDER BY id DESC")
    List<UserAnswer> getAllUserAnswers();

    @Query("SELECT * FROM user_answers WHERE serverId IS NULL")
    List<UserAnswer> getPendingSyncAnswers();

    @Query("SELECT * FROM user_answers WHERE serverId IS NOT NULL")
    List<UserAnswer> getSyncedAnswers();

    @Query("SELECT COUNT(*) FROM user_answers WHERE serverId IS NULL")
    int getPendingSyncCount();

    @Query("UPDATE user_answers SET serverId = :serverId WHERE id = :id")
    void updateServerId(int id, String serverId);

    @Query("DELETE FROM user_answers WHERE quizHistoryId = :quizHistoryId")
    void deleteUserAnswersByHistoryId(int quizHistoryId);
}
