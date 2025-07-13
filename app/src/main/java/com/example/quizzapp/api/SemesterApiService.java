package com.example.quizzapp.api;
import com.example.quizzapp.models.Semester;
import com.example.quizzapp.models.api.ApiResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
//import retrofit2.http.Path;
public interface SemesterApiService {
    @GET("student/semesters/active")
    Call<ApiResponse<List<Semester>>> getAllSemester();
}
