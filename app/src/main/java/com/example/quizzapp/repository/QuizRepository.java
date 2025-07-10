package com.example.quizzapp.repository;

import android.content.Context;

import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.QuizApiService;
import com.example.quizzapp.database.QuizDatabase;
import com.example.quizzapp.models.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizRepository {
    private static final String TAG = "QuizRepository";
    private QuizDatabase database;
    private QuizApiService apiService;
    private ExecutorService executor;

    public QuizRepository(Context context) {
        database = QuizDatabase.getInstance(context);
        apiService = ApiClient.getApiService();
        executor = Executors.newFixedThreadPool(4);
    }
    public void login(String username, String password, AuthCallback callback) {
        QuizApiService.LoginRequest request = new QuizApiService.LoginRequest(username, password);
        apiService.login(request).enqueue(new Callback<QuizApiService.ApiResponse<User>>() {
            @Override
            public void onResponse(Call<QuizApiService.ApiResponse<User>> call, Response<QuizApiService.ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    // Save user to local database
                    executor.execute(() -> {
                        database.userDao().logoutAllUsers();
                        database.userDao().insertUser(user);
                        database.userDao().setUserLoggedIn(user.getId());
                    });
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Login failed";
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<QuizApiService.ApiResponse<User>> call, Throwable t) {
                // Try offline login
                executor.execute(() -> {
                    User user = database.userDao().loginUser(username, password);
                    if (user != null) {
                        database.userDao().logoutAllUsers();
                        database.userDao().setUserLoggedIn(user.getId());
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Login failed: " + t.getMessage());
                    }
                });
            }
        });
    }
    public void register(String username, String email, String password, String fullName, AuthCallback callback) {
        QuizApiService.RegisterRequest request = new QuizApiService.RegisterRequest(username, email, password, fullName);
        apiService.register(request).enqueue(new Callback<QuizApiService.ApiResponse<User>>() {
            @Override
            public void onResponse(Call<QuizApiService.ApiResponse<User>> call, Response<QuizApiService.ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    // Save user to local database
                    executor.execute(() -> {
                        database.userDao().insertUser(user);
                    });
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Registration failed";
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<QuizApiService.ApiResponse<User>> call, Throwable t) {
                callback.onError("Registration failed: " + t.getMessage());
            }
        });
    }
    public void getCurrentUser(UserCallback callback) {
        executor.execute(() -> {
            User user = database.userDao().getLoggedInUser();
            callback.onSuccess(user);
        });
    }
    public void getLoggedInUser(UserCallback callback) {
        getCurrentUser(callback);
    }

    // Logout user
    public void logout() {
        executor.execute(() -> {
            database.userDao().logoutAllUsers();
        });
    }

    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }
    public interface UserCallback {
        void onSuccess(User user);
        void onError(String error);
    }
}
