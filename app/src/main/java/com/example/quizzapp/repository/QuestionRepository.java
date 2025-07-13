package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.QuizApiService;
import com.example.quizzapp.models.Question;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository chuyên xử lý Question (Lấy danh sách câu hỏi theo subject)
 */
public class QuestionRepository {
    private static final String TAG = "QuestionRepository";
    private QuizApiService quizApiService;

    public QuestionRepository(Context context) {
        quizApiService = ApiClient.getQuizApiService(context);
    }

    /**
     * Lấy danh sách câu hỏi theo subject code
     */
    public void getQuestionsBySubject(String subjectCode, QuestionListCallback callback) {
        quizApiService.getQuestionsBySubject(subjectCode).enqueue(new Callback<ApiResponse<List<Question>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Question>>> call, Response<ApiResponse<List<Question>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Question> questions = response.body().getData();

                    Log.d(TAG, "Loaded " + questions.size() + " questions for subject: " + subjectCode);
                    callback.onSuccess(questions);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "No questions found for this subject";
                    Log.e(TAG, "Error response: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Question>>> call, Throwable t) {
                Log.e(TAG, "Get questions by subject API call failed: " + t.getMessage());
                callback.onError("Network error. Please check your connection.");
            }
        });
    }

    // Callback interface cho Question
    public interface QuestionListCallback {
        void onSuccess(List<Question> questions);
        void onError(String error);
    }
}