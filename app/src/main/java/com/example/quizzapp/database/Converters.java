package com.example.quizzapp.database;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Converters {

    private static final Gson gson = new Gson();

    /**
     * Chuyển đổi Map<Integer, String> thành JSON String để lưu vào database
     */
    @TypeConverter
    public static String fromUserAnswersMap(Map<Integer, String> userAnswers) {
        if (userAnswers == null || userAnswers.isEmpty()) {
            return null;
        }
        return gson.toJson(userAnswers);
    }

    /**
     * Chuyển đổi JSON String thành Map<Integer, String>
     */
    @TypeConverter
    public static Map<Integer, String> toUserAnswersMap(String userAnswersString) {
        if (userAnswersString == null || userAnswersString.isEmpty()) {
            return new HashMap<>();
        }

        try {
            Type type = new TypeToken<Map<Integer, String>>(){}.getType();
            Map<Integer, String> result = gson.fromJson(userAnswersString, type);
            return result != null ? result : new HashMap<>();
        } catch (Exception e) {
            // Nếu có lỗi parse, trả về map rỗng
            return new HashMap<>();
        }
    }
}