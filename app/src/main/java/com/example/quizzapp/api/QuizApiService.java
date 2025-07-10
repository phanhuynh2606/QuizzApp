package com.example.quizzapp.api;


import com.example.quizzapp.models.User;
import com.example.quizzapp.models.Quiz;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface QuizApiService {
    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);

    // Quiz endpoints
    @GET("quizzes")
    Call<ApiResponse<List<Quiz>>> getQuizzes();

    class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private int code;

        public ApiResponse() {}

        public ApiResponse(boolean success, String message, T data) {
            this.success = success;
            this.message = message;
            this.data = data;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
    }
    class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    class RegisterRequest {
        private String username;
        private String email;
        private String password;
        private String fullName;

        public RegisterRequest(String username, String email, String password, String fullName) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.fullName = fullName;
        }

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
    }

}
