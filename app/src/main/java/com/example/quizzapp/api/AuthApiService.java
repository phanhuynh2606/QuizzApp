package com.example.quizzapp.api;

import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.LoginRequest;
import com.example.quizzapp.models.api.RegisterRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * API Service chuyên xử lý Authentication
 */
public interface AuthApiService {

    @POST("auth/login")
    Call<ApiResponse<User>> login(@Body LoginRequest loginRequest);

    @POST("auth/register")
    Call<ApiResponse<User>> register(@Body RegisterRequest registerRequest);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();
}
