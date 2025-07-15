package com.example.quizzapp.api;


import com.example.quizzapp.models.PracticeHistory;
import com.example.quizzapp.models.Question;
import com.example.quizzapp.models.Quiz;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * API Service chuyên xử lý Quiz
 */
public interface QuizApiService {
//    @GET("/quizzes")
//    Call<ApiResponse<List<Quiz>>> getAllQuizzes();
//
//    @GET("quizzes/{id}")
//    Call<ApiResponse<Quiz>> getQuizById(@Path("id") String quizId);

    @GET("student/exams/{subjectId}")
    Call<ApiResponse<List<Quiz>>> getQuizzesBySubject(@Path("subjectId") String subject);

    @GET("student/questions/{subjectCode}")
    Call<ApiResponse<List<Question>>> getQuestionsBySubject(@Path("subjectCode") String subjectCode);

    @POST("student/exam/practice-history")
    Call<ApiResponse<PracticeHistory>> savePracticeHistory(@Body PracticeHistory request);

    @GET("student/exam/practice-history/{userId}")
    Call<ApiResponse<List<PracticeHistory>>> getExamHistory(@Path("userId") String userId);

//    @GET("quizzes/seme/{authorId}")
//    Call<ApiResponse<List<Quiz>>> getQuizzesByAuthor(@Path("authorId") String authorId);
}
