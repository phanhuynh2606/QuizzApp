package com.example.quizzapp.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.quizzapp.activities.LoginActivity;
import com.example.quizzapp.api.ApiClient;
import com.example.quizzapp.api.AuthApiService;
import com.example.quizzapp.database.QuizDatabase;
import com.example.quizzapp.database.UserDao;
import com.example.quizzapp.models.User;
import com.example.quizzapp.models.api.ApiResponse;
import com.example.quizzapp.models.api.ChangePasswordRequest;
import com.example.quizzapp.models.api.LoginRequest;
import com.example.quizzapp.models.api.RegisterRequest;
import com.example.quizzapp.models.api.RegisterResponse;
import com.example.quizzapp.models.api.LoginResponse;
import com.example.quizzapp.models.api.RefreshTokenRequest;
import com.example.quizzapp.utils.Constants;
import com.example.quizzapp.utils.LoginUtils;
import com.example.quizzapp.utils.PasswordUtils;
import com.example.quizzapp.utils.TokenManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.gson.Gson;

/**
 * Repository chuyên xử lý Authentication (Đăng nhập, Đăng ký, Đăng xuất)
 */
public class AuthRepository {
    private static final String TAG = "AuthRepository";

    private QuizDatabase database;
    private AuthApiService authApiService;
    private UserDao userDao;
    private ExecutorService executor;
    private TokenManager tokenManager; // Thêm TokenManager
    private Gson gson; // Thêm Gson để parse error response

    public AuthRepository(Context context) {
        database = QuizDatabase.getInstance(context);
        authApiService = ApiClient.getAuthApiService(context); // Đã fix
        userDao = database.userDao();
        executor = Executors.newSingleThreadExecutor();
        tokenManager = new TokenManager(context);
        gson = new Gson(); // Khởi tạo Gson
    }

    /**
     * Parse error message từ response body hoặc error body
     */
    private String parseErrorMessage(Response<?> response) {
        String message = "Unknown error occurred";

        try {
            // Kiểm tra response body trước (cho trường hợp server trả về 200 nhưng success = false)
            if (response.body() != null && response.body() instanceof ApiResponse) {
                ApiResponse<?> apiResponse = (ApiResponse<?>) response.body();
                if (!apiResponse.isSuccess() && apiResponse.getMessage() != null) {
                    message = apiResponse.getMessage();
                    Log.d(TAG, "Error from response body: " + message);
                    return message;
                }
            }

            // Kiểm tra error body (cho trường hợp HTTP error status)
            if (response.errorBody() != null) {
                String errorBodyString = response.errorBody().string();
                Log.e(TAG, "Error response body: " + errorBodyString);

                // Parse JSON error response
                ApiResponse<?> errorResponse = gson.fromJson(errorBodyString, ApiResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null) {
                    message = errorResponse.getMessage();
                } else {
                    // Fallback: sử dụng raw error body nếu không parse được
                    message = errorBodyString;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error message: " + e.getMessage());
            // Fallback based on HTTP status code
            switch (response.code()) {
                case 400:
                    message = "Bad request - please check your input";
                    break;
                case 401:
                    message = "Invalid credentials";
                    break;
                case 403:
                    message = "Access denied";
                    break;
                case 404:
                    message = "Service not found";
                    break;
                case 500:
                    message = "Server error - please try again later";
                    break;
                default:
                    message = "Network error (Code: " + response.code() + ")";
            }
        }

        return message;
    }

    /**
     * Đăng nhập với email
     * @param loginInput Email
     * @param password Mật khẩu
     * @param callback Callback trả kết quả
     */
    public void login(String loginInput, String password, AuthCallback callback) {
        // Validate input trước khi gọi API
        if (!LoginUtils.isValidLoginInput(loginInput)) {
            callback.onError("Please enter a valid email or username");
            return;
        }

        if (!LoginUtils.isValidPassword(password)) {
            callback.onError("Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        // Tạo request để gửi lên server
        LoginRequest request = new LoginRequest(loginInput, password);

        // Log request để debug
        Log.d(TAG, "Sending login request - loginInput: " + loginInput + ", password length: " + password.length());

        // Gọi API đăng nhập
        authApiService.login(request).enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                Log.d(TAG, "onResponse: "+ response.toString());
                Log.d(TAG, "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    LoginResponse loginResponse = response.body().getData();
                    User user = loginResponse.getUser();

                    // Lưu tokens vào TokenManager
                    tokenManager.saveTokens(
                        loginResponse.getAccessToken(),
                        loginResponse.getRefreshToken(),
                        loginResponse.getTokenType(),
                        loginResponse.getExpiresIn()
                    );

                    // Hash password trước khi lưu để bảo mật
                    PasswordUtils.HashedPassword hashedPassword = PasswordUtils.hashPasswordWithSalt(password);
                    user.setHashedPassword(hashedPassword.getHashedPassword());
                    user.setSalt(hashedPassword.getSalt());

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Login successful for user: " + user.getEmail());
                    callback.onSuccess(user);
                } else {
                    // Parse error message từ API response
                    String message = parseErrorMessage(response);

                    // Kiểm tra loại lỗi để quyết định có thử offline không
                    boolean shouldTryOffline = false;

                    if (response.code() >= 500) {
                        // Server error (5xx) - có thể thử offline
                        shouldTryOffline = true;
                    } else {
                        // Client error (4xx) - kiểm tra message để quyết định
                        String lowerMessage = message.toLowerCase();
                        if (lowerMessage.contains("invalid credentials") ||
                            lowerMessage.contains("incorrect") ||
                            lowerMessage.contains("wrong password") ||
                            lowerMessage.contains("user not found") ||
                            lowerMessage.contains("deactivated") ||
                            lowerMessage.contains("suspended") ||
                            lowerMessage.contains("banned")) {
                            // Lỗi account/credentials - không thử offline
                            shouldTryOffline = false;
                        } else {
                            // Lỗi khác - có thể thử offline
                            shouldTryOffline = true;
                        }
                    }

                    if (shouldTryOffline) {
                        Log.d(TAG, "Server error, trying offline login");
                        tryOfflineLogin(loginInput, password, callback, message);
                    } else {
                        Log.d(TAG, "Authentication failed, not trying offline: " + message);
                        callback.onError(message);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                Log.e(TAG, "Login API call failed: " + t.getMessage());
                // Thử đăng nhập offline khi không có internet
                tryOfflineLogin(loginInput, password, callback, "No internet connection");
            }
        });
    }

    /**
     * Thử đăng nhập offline từ database local
     */
    private void tryOfflineLogin(String loginInput, String password, AuthCallback callback, String apiError) {
        executor.execute(() -> {
            try {
                // Tìm user theo email
                User user = userDao.getUserForPasswordVerification(loginInput);

                if (user != null) {
                    // Xác thực password bằng cách hash password nhập vào với salt đã lưu
                    boolean isPasswordValid = PasswordUtils.verifyPassword(
                        password,
                        user.getHashedPassword(),
                        user.getSalt()
                    );

                    if (isPasswordValid) {
                        // Cập nhật trạng thái đăng nhập
                        userDao.logoutAllUsers();
                        userDao.setUserLoggedIn(user.getId());

                        Log.d(TAG, "Offline login successful for user: " + user.getEmail());
                        callback.onSuccess(user);
                    } else {
                        Log.w(TAG, "Offline login failed - invalid password");
                        callback.onError("Invalid email or password");
                    }
                } else {
                    Log.w(TAG, "Offline login failed - user not found in local database");
                    callback.onError(apiError + ". No cached credentials found.");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during offline login: " + e.getMessage());
                callback.onError("Login failed: " + e.getMessage());
            }
        });
    }

    /**
     * Đăng ký tài khoản mới
     */
    public void register(String email, String password, String fullName, AuthCallback callback) {
        RegisterRequest request = new RegisterRequest(email, password, fullName);

        authApiService.register(request).enqueue(new Callback<ApiResponse<RegisterResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<RegisterResponse>> call, Response<ApiResponse<RegisterResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    RegisterResponse registerResponse = response.body().getData();

                    // Lưu tokens vào TokenManager
                    tokenManager.saveTokens(
                        registerResponse.getAccessToken(),
                        registerResponse.getRefreshToken(),
                        registerResponse.getTokenType(),
                        registerResponse.getExpiresIn()
                    );

                    // Chuyển đổi RegisterResponse thành User object và lưu password để đăng nhập offline
                    User user = convertRegisterResponseToUser(registerResponse, email, fullName, password);

                    // Lưu user vào database local
                    saveUserToLocal(user);

                    Log.d(TAG, "Registration successful for user: " + user.getEmail());
                    callback.onSuccess(user);
                } else {
                    String message = response.body() != null ? response.body().getMessage() : "Registration failed";
                    Log.w(TAG, "Registration failed: " + message);
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<RegisterResponse>> call, Throwable t) {
                Log.e(TAG, "Registration API call failed: " + t.getMessage());
                callback.onError("Registration failed: " + t.getMessage());
            }
        });
    }

    /**
     * Chuyển đổi RegisterResponse thành User object
     */
    private User convertRegisterResponseToUser(RegisterResponse registerResponse, String email, String fullName, String password) {
        User user = new User();
        user.setId(registerResponse.getUser().getId());
        user.setEmail(email);
        user.setFullName(fullName);

        // Hash password trước khi lưu
        PasswordUtils.HashedPassword hashedPassword = PasswordUtils.hashPasswordWithSalt(password);
        user.setHashedPassword(hashedPassword.getHashedPassword());
        user.setSalt(hashedPassword.getSalt());

        user.setLoggedIn(true);
        // Sửa: setLocalCreatedAt nhận long, không phải setCreatedAt nhận String
        user.setLocalCreatedAt(System.currentTimeMillis());

        return user;
    }

    /**
     * Lưu thông tin user vào database local (public method)
     * @param user User object cần lưu
     * @param callback Callback trả kết quả
     */
    public void saveUser(User user, AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Xóa tất cả user cũ để đảm bảo chỉ có user hiện tại trong DB
                userDao.deleteAll();

                // Đặt trạng thái đăng nhập cho user mới
                user.setLoggedIn(true);

                // Lưu user mới vào DB
                userDao.insertUser(user);

                Log.d(TAG, "User saved to local database: " + user.getId());
                callback.onSuccess(user);
            } catch (Exception e) {
                Log.e(TAG, "Error saving user to local database: " + e.getMessage());
                callback.onError("Failed to save user: " + e.getMessage());
            }
        });
    }

    /**
     * Lưu thông tin user vào database local.
     * Phương thức này sẽ xóa tất cả người dùng cũ và chỉ lưu người dùng hiện tại.
     */
    private void saveUserToLocal(User user) {
        executor.execute(() -> {
            try {
                // Xóa tất cả user cũ để đảm bảo chỉ có user hiện tại trong DB
                userDao.deleteAll();

                // Đặt trạng thái đăng nhập cho user mới
                user.setLoggedIn(true);

                // Lưu user mới vào DB
                userDao.insertUser(user);

                Log.d(TAG, "User saved to local database: " + user.getId());
            } catch (Exception e) {
                Log.e(TAG, "Error saving user to local database: " + e.getMessage());
            }
        });
    }

    /**
     * Đăng xuất
     */
    public void logout(AuthCallback callback) {
        executor.execute(() -> {
            try {
                // Đăng xuất tất cả user trong database local
                userDao.logoutAllUsers();

                // Clear stored tokens
                tokenManager.clearTokens();

                Log.d(TAG, "User logged out successfully");
                callback.onSuccess(null);
            } catch (Exception e) {
                Log.e(TAG, "Error during logout: " + e.getMessage());
                callback.onError("Logout failed: " + e.getMessage());
            }
        });
    }

    /**
     * Lấy user hiện tại đang đăng nhập
     */
    public void getCurrentUser(UserCallback callback) {
        executor.execute(() -> {
            try {
                User user = userDao.getLoggedInUser();
                callback.onResult(user);
            } catch (Exception e) {
                Log.e(TAG, "Error getting current user: " + e.getMessage());
                callback.onResult(null);
            }
        });
    }

    /**
     * Kiểm tra xem có user nào đang đăng nhập không
     */
    public boolean isLoggedIn() {
        // Sử dụng TokenManager thay vì database để tránh main thread blocking
        return tokenManager.hasToken();
    }

    /**
     * Đổi mật khẩu - Đơn giản hóa vì TokenInterceptor đã xử lý refresh token tự động
     */
    public void changePassword(String currentPassword, String newPassword, AuthCallback callback) {
        // Validate input
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            callback.onError("Current password is required");
            return;
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            callback.onError("New password is required");
            return;
        }

        if (newPassword.length() < Constants.MIN_PASSWORD_LENGTH) {
            callback.onError("New password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters");
            return;
        }

        // Kiểm tra có token không
        if (!tokenManager.hasToken()) {
            callback.onError("You are not logged in. Please login again.");
            return;
        }

        ChangePasswordRequest request = new ChangePasswordRequest(currentPassword, newPassword);
        Log.d(TAG, "Sending change password request");

        // TokenInterceptor sẽ tự động thêm Authorization header và xử lý refresh token
        authApiService.changePassword(request).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                Log.d(TAG, "Change password response code: " + response.code());

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Log.d(TAG, "Password changed successfully on server");

                    // Cập nhật password trong database local
                    updatePasswordInLocalDatabase(newPassword, callback);

                } else if (response.code() == 401) {
                    // Nếu vẫn 401 sau khi TokenInterceptor đã thử refresh, có nghĩa là refresh token hết hạn
                    Log.e(TAG, "401 after token refresh attempt - session expired");
                    tokenManager.clearTokens();
                    callback.onError("Session expired. Please login again.");
                } else {
                    // Xử lý các lỗi khác
                    String message = "Failed to change password";
                    if (response.body() != null && response.body().getMessage() != null) {
                        message = response.body().getMessage();
                    } else if (response.errorBody() != null) {
                        try {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Change password error: " + errorBody);
                            // Parse error message from server
                            if (errorBody.contains("Current password is incorrect")) {
                                message = "Current password is incorrect";
                            } else if (errorBody.contains("Password")) {
                                message = "Password validation failed";
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }
                    }
                    callback.onError(message);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Log.e(TAG, "Change password API call failed: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Cập nhật password trong database local sau khi đổi mật khẩu thành công
     */
    private void updatePasswordInLocalDatabase(String newPassword, AuthCallback callback) {
        executor.execute(() -> {
            try {
                User currentUser = userDao.getLoggedInUser();
                if (currentUser != null) {
                    // Hash password mới với salt mới
                    PasswordUtils.HashedPassword hashedPassword = PasswordUtils.hashPasswordWithSalt(newPassword);

                    // Cập nhật password và salt trong database
                    userDao.updateUserPassword(
                        currentUser.getId(),
                        hashedPassword.getHashedPassword(),
                        hashedPassword.getSalt()
                    );

                    Log.d(TAG, "Password updated in local database");
                    callback.onSuccess(null);
                } else {
                    Log.e(TAG, "No current user found in database");
                    callback.onError("Failed to update local password");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating password in local database: " + e.getMessage());
                callback.onError("Failed to update local password: " + e.getMessage());
            }
        });
    }


    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String error);
    }

    public interface UserCallback {
        void onResult(User user);
    }
}
