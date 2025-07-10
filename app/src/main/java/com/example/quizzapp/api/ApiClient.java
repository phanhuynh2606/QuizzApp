package com.example.quizzapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:5000/api/"; // Thay đổi URL này
    private static Retrofit retrofit;
    private static QuizApiService quizApiService;
    private static AuthApiService authApiService;

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static QuizApiService getQuizApiService() {
        if (quizApiService == null) {
            quizApiService = getRetrofitInstance().create(QuizApiService.class);
        }
        return quizApiService;
    }

    public static AuthApiService getAuthApiService() {
        if (authApiService == null) {
            authApiService = getRetrofitInstance().create(AuthApiService.class);
        }
        return authApiService;
    }

    // Deprecated - sử dụng getQuizApiService() thay thế
    @Deprecated
    public static QuizApiService getApiService() {
        return getQuizApiService();
    }
}
