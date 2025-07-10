package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.QuizApiService;
import com.example.quizzapp.database.QuizDatabase;
import com.example.quizzapp.database.QuizDao;
import com.example.quizzapp.models.Quiz;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository chuyên xử lý Quiz (Lấy danh sách quiz, nộp bài, lịch sử)
 */
public class QuizRepository {
    private static final String TAG = "QuizRepository";
    private QuizDatabase database;
    private QuizApiService quizApiService;
    private QuizDao quizDao;
    private ExecutorService executor;

    public QuizRepository(Context context) {
        database = QuizDatabase.getInstance(context);
        quizApiService = ApiClient.getQuizApiService(); // Sử dụng method mới
        quizDao = database.quizDao();
        executor = Executors.newFixedThreadPool(4);
    }

    /**
     * Lấy danh sách tất cả quiz từ API
     */
    public void getAllQuizzes(QuizListCallback callback) {
        quizApiService.getAllQuizzes().enqueue(new Callback<ApiResponse<List<Quiz>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Quiz>>> call, Response<ApiResponse<List<Quiz>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Quiz> quizzes = response.body().getData();

                    // Cache quizzes vào database local
                    cacheQuizzesToLocal(quizzes);

                    Log.d(TAG, "Loaded " + quizzes.size() + " quizzes from API");
                    callback.onSuccess(quizzes);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Failed to load quizzes";

                    // Thử lấy từ cache local nếu API thất bại
                    loadQuizzesFromLocal(callback, message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Quiz>>> call, Throwable t) {
                Log.e(TAG, "Get quizzes API call failed: " + t.getMessage());

                // Thử lấy từ cache local khi không có internet
                loadQuizzesFromLocal(callback, "No internet connection");
            }
        });
    }

    /**
     * Lấy quiz theo ID từ API
     */
    public void getQuizById(String quizId, QuizCallback callback) {
        quizApiService.getQuizById(quizId).enqueue(new Callback<ApiResponse<Quiz>>() {
            @Override
            public void onResponse(Call<ApiResponse<Quiz>> call, Response<ApiResponse<Quiz>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Quiz quiz = response.body().getData();

                    Log.d(TAG, "Loaded quiz: " + quiz.getTitle());
                    callback.onSuccess(quiz);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Quiz not found";
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Quiz>> call, Throwable t) {
                Log.e(TAG, "Get quiz by ID API call failed: " + t.getMessage());
                callback.onError("Network error. Please check your connection.");
            }
        });
    }

    /**
     * Lấy quiz theo subject
     */
    public void getQuizzesBySubject(String subject, QuizListCallback callback) {
        quizApiService.getQuizzesBySubject(subject).enqueue(new Callback<ApiResponse<List<Quiz>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Quiz>>> call, Response<ApiResponse<List<Quiz>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Quiz> quizzes = response.body().getData();

                    Log.d(TAG, "Loaded " + quizzes.size() + " quizzes for subject: " + subject);
                    callback.onSuccess(quizzes);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "No quizzes found for this subject";
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Quiz>>> call, Throwable t) {
                Log.e(TAG, "Get quizzes by subject API call failed: " + t.getMessage());
                callback.onError("Network error. Please check your connection.");
            }
        });
    }

    /**
     * Cache danh sách quiz vào database local
     */
    private void cacheQuizzesToLocal(List<Quiz> quizzes) {
        executor.execute(() -> {
            try {
                quizDao.insertQuizzes(quizzes);
                Log.d(TAG, "Cached " + quizzes.size() + " quizzes to local database");
            } catch (Exception e) {
                Log.e(TAG, "Error caching quizzes to local database: " + e.getMessage());
            }
        });
    }

    /**
     * Lấy quiz từ cache local khi API thất bại
     */
    private void loadQuizzesFromLocal(QuizListCallback callback, String apiError) {
        executor.execute(() -> {
            try {
                List<Quiz> cachedQuizzes = quizDao.getAllQuizzes();

                if (cachedQuizzes != null && !cachedQuizzes.isEmpty()) {
                    Log.d(TAG, "Loaded " + cachedQuizzes.size() + " quizzes from local cache");
                    callback.onSuccess(cachedQuizzes);
                } else {
                    Log.w(TAG, "No cached quizzes found");
                    callback.onError(apiError + ". No cached data available.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading quizzes from local cache: " + e.getMessage());
                callback.onError("Failed to load quizzes: " + e.getMessage());
            }
        });
    }

    // Callback interfaces cho Quiz
    public interface QuizListCallback {
        void onSuccess(List<Quiz> quizzes);
        void onError(String error);
    }

    public interface QuizCallback {
        void onSuccess(Quiz quiz);
        void onError(String error);
    }
}
