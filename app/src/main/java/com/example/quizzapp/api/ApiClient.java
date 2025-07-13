package com.example.quizzapp.api;

import android.content.Context;

import com.example.quizzapp.utils.Constants;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit;
    private static QuizApiService quizApiService;
    private static AuthApiService authApiService;

    public static Retrofit getRetrofitInstance(Context context) {
        if (retrofit == null) {
            // Tạo OkHttpClient với TokenInterceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new TokenInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static QuizApiService getQuizApiService(Context context) {
        if (quizApiService == null) {
            quizApiService = getRetrofitInstance(context).create(QuizApiService.class);
        }
        return quizApiService;
    }

    public static AuthApiService getAuthApiService(Context context) {
        if (authApiService == null) {
            authApiService = getRetrofitInstance(context).create(AuthApiService.class);
        }
        return authApiService;
    }

    public static SemesterApiService getSemesterService(Context context) {
        return getRetrofitInstance(context).create(SemesterApiService.class);
    }

    public static SubjectApiService getSubjectService(Context context) {
        return getRetrofitInstance(context).create(SubjectApiService.class);
    }

    // Deprecated methods for backward compatibility
    @Deprecated
    public static Retrofit getRetrofitInstance() {
        throw new IllegalStateException("Use getRetrofitInstance(Context) instead");
    }

    @Deprecated
    public static QuizApiService getQuizApiService() {
        throw new IllegalStateException("Use getQuizApiService(Context) instead");
    }

    @Deprecated
    public static AuthApiService getAuthApiService() {
        throw new IllegalStateException("Use getAuthApiService(Context) instead");
    }

    @Deprecated
    public static QuizApiService getApiService() {
        throw new IllegalStateException("Use getQuizApiService(Context) instead");
    }

    @Deprecated
    public static SemesterApiService getSemesterService() {
        throw new IllegalStateException("Use getSemesterService(Context) instead");
    }
    @Deprecated
    public static SubjectApiService getSubjectService() {
        throw new IllegalStateException("Use getSubjectService(Context) instead");
    }
}
