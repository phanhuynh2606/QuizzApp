package com.example.quizzapp.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.quizzapp.database.QuizDatabase;
import com.example.quizzapp.database.QuizStateDao;
import com.example.quizzapp.models.QuizState;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QuizStateRepository {

    private static final String TAG = "QuizStateRepository";
    private QuizStateDao quizStateDao;
    private ExecutorService executor;

    public QuizStateRepository(Context context) {
        QuizDatabase database = QuizDatabase.getInstance(context);
        quizStateDao = database.quizStateDao();
        executor = Executors.newFixedThreadPool(2);
    }

    // Callback interfaces
    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface LoadCallback {
        void onSuccess(QuizState quizState);
        void onError(String error);
    }

    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface ListCallback {
        void onSuccess(List<QuizState> quizStates);
        void onError(String error);
    }

    /**
     * Lưu hoặc cập nhật quiz state
     */
    public void saveQuizState(QuizState quizState, SaveCallback callback) {
        executor.execute(() -> {
            try {
                quizStateDao.insertOrUpdate(quizState);
                if (callback != null) {
                    callback.onSuccess();
                }
                Log.d(TAG, "Quiz state saved successfully: " + quizState.getQuizId());
            } catch (Exception e) {
                Log.e(TAG, "Error saving quiz state", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Lưu quiz state với parameters
     */
    public void saveQuizState(String quizId, String subjectCode, String quizTitle,
                              int currentQuestionIndex, long timeLeftInMillis,
                              Map<Integer, String> userAnswers, SaveCallback callback) {

        QuizState quizState = new QuizState(quizId, subjectCode, quizTitle,
                currentQuestionIndex, timeLeftInMillis, userAnswers);

        saveQuizState(quizState, callback);
    }

    /**
     * Lấy quiz state theo ID
     */
    public void getQuizStateById(String quizId, LoadCallback callback) {
        executor.execute(() -> {
            try {
                QuizState quizState = quizStateDao.getQuizStateById(quizId);
                if (callback != null) {
                    callback.onSuccess(quizState);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading quiz state by ID", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Lấy quiz state theo subject code
     */
    public void getQuizStateBySubject(String subjectCode, LoadCallback callback) {
        executor.execute(() -> {
            try {
                QuizState quizState = quizStateDao.getQuizStateBySubject(subjectCode);

                // Kiểm tra nếu state đã hết hạn
                if (quizState != null && quizState.isExpired()) {
                    quizStateDao.deleteById(quizState.getQuizId());
                    quizState = null;
                }

                if (callback != null) {
                    callback.onSuccess(quizState);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading quiz state by subject", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Xóa quiz state theo ID
     */
    public void deleteQuizState(String quizId, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                int deletedRows = quizStateDao.deleteById(quizId);
                if (callback != null) {
                    callback.onSuccess();
                }
                Log.d(TAG, "Quiz state deleted: " + deletedRows + " rows affected");
            } catch (Exception e) {
                Log.e(TAG, "Error deleting quiz state", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Xóa quiz state theo subject code
     */
    public void deleteQuizStateBySubject(String subjectCode, DeleteCallback callback) {
        executor.execute(() -> {
            try {
                int deletedRows = quizStateDao.deleteBySubject(subjectCode);
                if (callback != null) {
                    callback.onSuccess();
                }
                Log.d(TAG, "Quiz states deleted for subject " + subjectCode + ": " + deletedRows + " rows");
            } catch (Exception e) {
                Log.e(TAG, "Error deleting quiz states by subject", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Kiểm tra có quiz state hợp lệ không
     */
    public void hasValidQuizState(String subjectCode, LoadCallback callback) {
        executor.execute(() -> {
            try {
                long expiredTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24h ago
                boolean hasValid = quizStateDao.hasValidQuizState(subjectCode, expiredTime);

                QuizState result = null;
                if (hasValid) {
                    result = quizStateDao.getQuizStateBySubject(subjectCode);
                }

                if (callback != null) {
                    callback.onSuccess(result);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking valid quiz state", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Lấy tất cả quiz states hợp lệ
     */
    public void getAllValidQuizStates(ListCallback callback) {
        executor.execute(() -> {
            try {
                long expiredTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24h ago
                List<QuizState> quizStates = quizStateDao.getValidQuizStates(expiredTime);

                if (callback != null) {
                    callback.onSuccess(quizStates);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error loading valid quiz states", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Xóa các states đã hết hạn
     */
    public void cleanupExpiredStates(DeleteCallback callback) {
        executor.execute(() -> {
            try {
                long expiredTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24h ago
                int deletedRows = quizStateDao.deleteExpiredStates(expiredTime);

                if (callback != null) {
                    callback.onSuccess();
                }
                Log.d(TAG, "Cleaned up " + deletedRows + " expired quiz states");
            } catch (Exception e) {
                Log.e(TAG, "Error cleaning up expired states", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Cập nhật thời gian còn lại
     */
    public void updateTimeLeft(String quizId, long timeLeft, SaveCallback callback) {
        executor.execute(() -> {
            try {
                long timestamp = System.currentTimeMillis();
                int updatedRows = quizStateDao.updateTimeLeft(quizId, timeLeft, timestamp);

                if (callback != null) {
                    callback.onSuccess();
                }
                Log.d(TAG, "Time left updated: " + updatedRows + " rows affected");
            } catch (Exception e) {
                Log.e(TAG, "Error updating time left", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Cập nhật câu hỏi hiện tại
     */
    public void updateCurrentQuestion(String quizId, int questionIndex, SaveCallback callback) {
        executor.execute(() -> {
            try {
                long timestamp = System.currentTimeMillis();
                int updatedRows = quizStateDao.updateCurrentQuestion(quizId, questionIndex, timestamp);

                if (callback != null) {
                    callback.onSuccess();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error updating current question", e);
                if (callback != null) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    /**
     * Đóng executor khi không cần thiết
     */
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}