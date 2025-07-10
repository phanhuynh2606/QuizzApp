package com.example.quizzapp.models.api;

/**
 * Generic API Response wrapper cho tất cả API calls
 */
public class ApiResponse<T> {
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

    public ApiResponse(boolean success, String message, T data, int code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
}
