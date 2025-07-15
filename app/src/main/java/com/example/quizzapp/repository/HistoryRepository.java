package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.QuizApiService;
import com.example.quizzapp.models.PracticeHistory;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository chuyên xử lý ExamHistory (Lấy lịch sử bài thi)
 */
public class HistoryRepository {
    private static final String TAG = "ExamHistoryRepository";
    private QuizApiService quizApiService;

    public HistoryRepository(Context context) {
        quizApiService = ApiClient.getQuizApiService(context);
    }

    /**
     * Lấy exam history cho user hiện tại
     */
    public void getExamHistory(String userId, ExamHistoryCallback callback) {
        quizApiService.getExamHistory(userId).enqueue(new Callback<ApiResponse<List<PracticeHistory>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<PracticeHistory>>> call, Response<ApiResponse<List<PracticeHistory>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<PracticeHistory> examHistories = response.body().getData();
                    Log.d(TAG, "Loaded " + examHistories.size() + " exam history records");
                    callback.onSuccess(examHistories);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to fetch exam history";
                    Log.e(TAG, "Error response: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<PracticeHistory>>> call, Throwable t) {
                Log.e(TAG, "Get exam history API call failed: " + t.getMessage());
                callback.onError("Network error. Please check your connection.");
            }
        });
    }
    // Callback interface cho ExamHistory
    public interface ExamHistoryCallback {
        void onSuccess(List<PracticeHistory> examHistories);
        void onError(String error);
    }
}