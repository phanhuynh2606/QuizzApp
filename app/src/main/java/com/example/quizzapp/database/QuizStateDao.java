package com.example.quizzapp.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.quizzapp.models.QuizState;

import java.util.List;

@Dao
public interface QuizStateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(QuizState quizState);
    @Query("SELECT * FROM quiz_states WHERE quizId = :quizId LIMIT 1")
    QuizState getQuizStateById(String quizId);

    @Query("SELECT * FROM quiz_states WHERE subjectCode = :subjectCode ORDER BY timestamp DESC LIMIT 1")
    QuizState getQuizStateBySubject(String subjectCode);

    @Query("SELECT * FROM quiz_states WHERE timestamp > :expiredTime ORDER BY timestamp DESC")
    List<QuizState> getValidQuizStates(long expiredTime);

    @Query("SELECT * FROM quiz_states ORDER BY timestamp DESC")
    List<QuizState> getAllQuizStates();

    @Update
    void update(QuizState quizState);

    @Query("DELETE FROM quiz_states WHERE quizId = :quizId")
    int deleteById(String quizId);

    @Query("DELETE FROM quiz_states WHERE subjectCode = :subjectCode")
    int deleteBySubject(String subjectCode);

    @Query("DELETE FROM quiz_states WHERE timestamp < :expiredTime")
    int deleteExpiredStates(long expiredTime);

    @Query("DELETE FROM quiz_states")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM quiz_states")
    int getQuizStateCount();

    @Query("SELECT COUNT(*) > 0 FROM quiz_states WHERE subjectCode = :subjectCode AND timestamp > :expiredTime")
    boolean hasValidQuizState(String subjectCode, long expiredTime);

    @Query("SELECT timeLeftInMillis FROM quiz_states WHERE subjectCode = :subjectCode ORDER BY timestamp DESC LIMIT 1")
    Long getTimeLeftBySubject(String subjectCode);

    @Query("UPDATE quiz_states SET timeLeftInMillis = :timeLeft, timestamp = :timestamp WHERE quizId = :quizId")
    int updateTimeLeft(String quizId, long timeLeft, long timestamp);

    @Query("UPDATE quiz_states SET currentQuestionIndex = :questionIndex, timestamp = :timestamp WHERE quizId = :quizId")
    int updateCurrentQuestion(String quizId, int questionIndex, long timestamp);

    @Query("UPDATE quiz_states SET userAnswers = :userAnswers, timestamp = :timestamp WHERE quizId = :quizId")
    int updateUserAnswers(String quizId, String userAnswers, long timestamp);

    @Query("SELECT DISTINCT subjectCode FROM quiz_states WHERE timestamp > :expiredTime")
    List<String> getActiveSubjectCodes(long expiredTime);

    @Query("SELECT * FROM quiz_states ORDER BY timestamp DESC LIMIT 1")
    QuizState getLatestQuizState();

    @Query("DELETE FROM quiz_states WHERE quizId NOT IN (SELECT quizId FROM quiz_states ORDER BY timestamp DESC LIMIT :keepCount)")
    int cleanupOldStates(int keepCount);
}