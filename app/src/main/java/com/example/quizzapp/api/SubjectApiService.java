package com.example.quizzapp.api;
import com.example.quizzapp.models.Subject;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface SubjectApiService {
    @GET("student/subjects/semester/{semesterId}")
    Call<ApiResponse<List<Subject>>> getAllSubjects(@Path("semesterId") String semesterId);

}
