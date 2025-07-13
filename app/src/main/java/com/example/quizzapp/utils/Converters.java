package com.example.quizzapp.utils;

import androidx.room.TypeConverter;
import com.example.quizzapp.models.ExamType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;

public class Converters {

    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromExamType(ExamType examType) {
        return gson.toJson(examType);
    }

    @TypeConverter
    public static ExamType toExamType(String value) {
        Type type = new TypeToken<ExamType>() {}.getType();
        return gson.fromJson(value, type);
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }
}
