package com.example.quizzapp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.quizzapp.R;
import com.example.quizzapp.models.AnswerRequest;
import com.example.quizzapp.models.PracticeHistory;
import com.example.quizzapp.models.Question;
import com.example.quizzapp.models.QuizState;
import com.example.quizzapp.models.User;
import com.example.quizzapp.repository.AuthRepository;
import com.example.quizzapp.repository.QuestionRepository;
import com.example.quizzapp.repository.QuizStateRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizTakeActivity extends AppCompatActivity {
    private static final String TAG = "QuizTakeActivity";
    private static final long QUIZ_DURATION = 15 * 60 * 1000; // 15 phút
    private static final long AUTO_SAVE_INTERVAL = 10 * 1000;
    private static final long NETWORK_CHECK_INTERVAL = 5 * 1000;

    // Views
    private TextView tvQuizTitle;
    private TextView tvTimer;
    private TextView tvQuestionNumber;
    private TextView tvProgress;
    private ProgressBar progressBar;
    private TextView tvQuestion;
    private RadioGroup radioGroup;
    private RadioButton rbOptionA, rbOptionB, rbOptionC, rbOptionD;
    private Button btnPrevious, btnNext, btnSubmit;
    private TextView tvAnsweredQuestions;
    private TextView tvRemainingTime;
    private TextView tvNetworkStatus;

    // Data
    private QuestionRepository questionRepository;
    private QuizStateRepository quizStateRepository;
    private List<Question> questions;
    private Map<Integer, String> userAnswers;
    private int currentQuestionIndex = 0;
    private String subjectCode;
    private String examId;
    private String quizTitle;
    private String quizId;
    private String examTypeCode;
    private String startTime;
    private AuthRepository authRepository;
    private User currentUser;


    // Timers and handlers
    private CountDownTimer countDownTimer;
    private Handler autoSaveHandler;
    private Handler networkCheckHandler;
    private long timeLeftInMillis = QUIZ_DURATION;

    // State management
    private boolean isQuizCompleted = false;
    private boolean hasUnsavedChanges = false;
    private boolean isNetworkAvailable = true;
    private boolean isResumedQuiz = false;
    private long lastSaveTime = 0;

    // Network monitoring
    private NetworkChangeReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_take);

        initViews();
        setupToolbar();
        initData();
        setupNetworkMonitoring();
        loadCurrentUser();
        // Khởi tạo repository
        quizStateRepository = new QuizStateRepository(this);
        // Kiểm tra quiz đang lưu trước khi load câu hỏi mới
        checkForResumableQuiz();
    }

    private void initViews() {
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber);
        tvProgress = findViewById(R.id.tvProgress);
        progressBar = findViewById(R.id.progressBar);
        tvQuestion = findViewById(R.id.tvQuestion);
        radioGroup = findViewById(R.id.radioGroup);
        rbOptionA = findViewById(R.id.rbOptionA);
        rbOptionB = findViewById(R.id.rbOptionB);
        rbOptionC = findViewById(R.id.rbOptionC);
        rbOptionD = findViewById(R.id.rbOptionD);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        tvAnsweredQuestions = findViewById(R.id.tvAnsweredQuestions);
        tvRemainingTime = findViewById(R.id.tvRemainingTime);
        tvNetworkStatus = findViewById(R.id.tv_network_status);

        // Set click listeners
        btnPrevious.setOnClickListener(v -> previousQuestion());
        btnNext.setOnClickListener(v -> nextQuestion());
        btnSubmit.setOnClickListener(v -> showSubmitDialog());

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Quiz");
            }
        }
    }
    private void loadCurrentUser() {
        authRepository.getCurrentUser(user -> {
            if (user != null) {
                currentUser = user;
            }
        });
    }
    private void initData() {
        questionRepository = new QuestionRepository(this);
        authRepository = new AuthRepository(this);

        questions = new ArrayList<>();
        userAnswers = new HashMap<>();

        // Khởi tạo handlers
        autoSaveHandler = new Handler(Looper.getMainLooper());
        networkCheckHandler = new Handler(Looper.getMainLooper());

        // Get data from intent
        Intent intent = getIntent();
        examId = intent.getStringExtra("quiz_id");
        subjectCode = intent.getStringExtra("subject_code");
        quizTitle = intent.getStringExtra("quiz_title");
        examTypeCode = intent.getStringExtra("examType");
        quizId = generateStateId();

        if (subjectCode == null) {
            Toast.makeText(this, "Subject code not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (quizTitle != null) {
            tvQuizTitle.setText(quizTitle);
        }
    }

    private void setupNetworkMonitoring() {
        networkReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);

        // Kiểm tra mạng định kỳ
        startNetworkCheck();
    }

    private void startNetworkCheck() {
        networkCheckHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkNetworkStatus();
                if (!isQuizCompleted) {
                    networkCheckHandler.postDelayed(this, NETWORK_CHECK_INTERVAL);
                }
            }
        }, NETWORK_CHECK_INTERVAL);
    }

    private void checkNetworkStatus() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if (isConnected != isNetworkAvailable) {
            isNetworkAvailable = isConnected;
            updateNetworkStatusUI();

            if (isConnected && hasUnsavedChanges) {
                // Khi có mạng trở lại, lưu ngay
                saveQuizStateIfNeeded();
            }
        }
    }

    private void updateNetworkStatusUI() {
        if (tvNetworkStatus != null) {
            if (isNetworkAvailable) {
                tvNetworkStatus.setText("Online");
                tvNetworkStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            } else {
                tvNetworkStatus.setText("Offline - Data saved locally");
                tvNetworkStatus.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }
        }
    }

    private void checkForResumableQuiz() {
        // Cleanup expired states trước
        quizStateRepository.cleanupExpiredStates(new QuizStateRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                checkValidQuizState();
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error cleaning up: " + error);
                checkValidQuizState();
            }
        });
    }

    private void checkValidQuizState() {
        quizStateRepository.hasValidQuizState(subjectCode, new QuizStateRepository.LoadCallback() {
            @Override
            public void onSuccess(QuizState quizState) {
                runOnUiThread(() -> {
                    if (quizState != null && quizState.hasTimeLeft()) {
                        showResumeDialog(quizState);
                    } else {
                        loadQuestions();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "Error checking quiz state: " + error);
                    loadQuestions();
                });
            }
        });
    }

    private void showResumeDialog(QuizState savedState) {
        String message = String.format(Locale.getDefault(),
                "You have an unfinished quiz:\n" +
                        "• Subject: %s\n" +
                        "• Question: %d\n" +
                        "• Answered: %d questions\n" +
                        "• Time left: %s\n\n" +
                        "Do you want to continue where you left off?",
                savedState.getSubjectCode(),
                savedState.getCurrentQuestionIndex() + 1,
                savedState.getAnsweredCount(),
                savedState.getFormattedTimeLeft());

        new AlertDialog.Builder(this)
                .setTitle("Resume Quiz")
                .setMessage(message)
                .setPositiveButton("Resume", (dialog, which) -> {
                    isResumedQuiz = true;
                    restoreFromState(savedState);
                })
                .setNegativeButton("Start New", (dialog, which) -> {
                    deleteCurrentQuizState();
                    loadQuestions();
                })
                .setCancelable(false)
                .show();
    }

    private void restoreFromState(QuizState savedState) {
        currentQuestionIndex = savedState.getCurrentQuestionIndex();
        timeLeftInMillis = savedState.getTimeLeftInMillis();
        userAnswers = new HashMap<>(savedState.getUserAnswers());

        Toast.makeText(this, "Quiz resumed successfully", Toast.LENGTH_SHORT).show();
        loadQuestions();
    }

    private void loadQuestions() {
        questionRepository.getQuestionsBySubject(subjectCode, new QuestionRepository.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questionList) {
                runOnUiThread(() -> {
                    questions.clear();
                    questions.addAll(questionList);

                    if (questions.isEmpty()) {
                        Toast.makeText(QuizTakeActivity.this, "No questions found for this subject", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }

                    setupQuiz();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QuizTakeActivity.this, "Error loading questions: " + error, Toast.LENGTH_SHORT).show();
                    finish();
                });
            }
        });
    }
    private void savePracticeHistory(String examId, String subjectCode, String examTypeCode, String userSessionId, String startTime, String endTime, int totalQuestions, int correctAnswers, double score,long timeSpent, List<AnswerRequest> answerList
    ) {
        PracticeHistory practiceHistory = new PracticeHistory(
                examId,
                subjectCode,
                examTypeCode,
                userSessionId,
                startTime,
                endTime,
                totalQuestions,
                correctAnswers,
                score,
                timeSpent,
                answerList,
                true
        );

        questionRepository.savePracticeHistory(practiceHistory, new QuestionRepository.SaveCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Log.d(TAG, "Saved practice history successfully");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QuizTakeActivity.this, "Failed to save history: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void setupQuiz() {
        startTime = getCurrentTimeISO();
        startTimer();
        displayQuestion();
        updateProgress();
        updateAnsweredCount();
        updateNavigationButtons();
        startAutoSave();
        updateNetworkStatusUI();

        // Lưu state ban đầu nếu là quiz mới
        if (!isResumedQuiz) {
            saveQuizStateIfNeeded();
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
                hasUnsavedChanges = true;
            }

            @Override
            public void onFinish() {
                Toast.makeText(QuizTakeActivity.this, "Time's up! Quiz submitted automatically.", Toast.LENGTH_SHORT).show();
                submitQuiz();
            }
        }.start();
    }

    private void startAutoSave() {
        autoSaveHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isQuizCompleted) {
                    saveQuizStateIfNeeded();
                    autoSaveHandler.postDelayed(this, AUTO_SAVE_INTERVAL);
                }
            }
        }, AUTO_SAVE_INTERVAL);
    }

    private void updateTimerDisplay() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeLeftFormatted);
        tvRemainingTime.setText("Time remaining: " + timeLeftFormatted);
    }

    private void displayQuestion() {
        if (currentQuestionIndex >= 0 && currentQuestionIndex < questions.size()) {
            Question question = questions.get(currentQuestionIndex);
            radioGroup.clearCheck();

            tvQuestion.setText(question.getQuestionText());
            String optionFormat = getString(R.string.option_format);

            rbOptionA.setText(String.format(optionFormat, "A", question.getOptions().get(0).getText()));
            rbOptionB.setText(String.format(optionFormat, "B", question.getOptions().get(1).getText()));
            rbOptionC.setText(String.format(optionFormat, "C", question.getOptions().get(2).getText()));
            rbOptionD.setText(String.format(optionFormat, "D", question.getOptions().get(3).getText()));

            String savedAnswer = userAnswers.get(currentQuestionIndex);
            if (savedAnswer != null) {
                switch (savedAnswer) {
                    case "A":
                        rbOptionA.setChecked(true);
                        break;
                    case "B":
                        rbOptionB.setChecked(true);
                        break;
                    case "C":
                        rbOptionC.setChecked(true);
                        break;
                    case "D":
                        rbOptionD.setChecked(true);
                        break;
                }
            }

            tvQuestionNumber.setText(String.format(Locale.getDefault(),
                    "Question %d of %d", currentQuestionIndex + 1, questions.size()));
        }
    }

    private void saveCurrentAnswer() {
        int checkedId = radioGroup.getCheckedRadioButtonId();
        if (checkedId != -1) {
            String answer = "";
            if (checkedId == R.id.rbOptionA) answer = "A";
            else if (checkedId == R.id.rbOptionB) answer = "B";
            else if (checkedId == R.id.rbOptionC) answer = "C";
            else if (checkedId == R.id.rbOptionD) answer = "D";

            userAnswers.put(currentQuestionIndex, answer);
            updateAnsweredCount();
            hasUnsavedChanges = true;
        }
    }

    private void saveQuizStateIfNeeded() {
        if (isQuizCompleted || !hasUnsavedChanges) return;

        //5s saved 1 lan
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSaveTime < 5000) {
            return;
        }

        saveCurrentAnswer();

        quizStateRepository.saveQuizState(
                quizId,
                subjectCode,
                quizTitle,
                currentQuestionIndex,
                timeLeftInMillis,
                userAnswers,
                new QuizStateRepository.SaveCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Quiz state saved successfully");
                        hasUnsavedChanges = false;
                        lastSaveTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error saving quiz state: " + error);
                        // Giữ hasUnsavedChanges = true để thử lại lần sau
                    }
                }
        );
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion();
            updateProgress();
            updateNavigationButtons();
            hasUnsavedChanges = true;
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion();
            updateProgress();
            updateNavigationButtons();
            hasUnsavedChanges = true;
        }
    }

    private void updateProgress() {
        if (!questions.isEmpty()) {
            int progress = (int) (((float) (currentQuestionIndex + 1) / questions.size()) * 100);
            progressBar.setProgress(progress);
            tvProgress.setText(progress + "%");
        }
    }

    private void updateAnsweredCount() {
        int answeredCount = userAnswers.size();
        tvAnsweredQuestions.setText(String.format(Locale.getDefault(),
                "Answered: %d/%d", answeredCount, questions.size()));
    }

    private void updateNavigationButtons() {
        btnPrevious.setEnabled(currentQuestionIndex > 0);

        boolean hasNext = currentQuestionIndex < questions.size() - 1;
        btnNext.setEnabled(hasNext);

        boolean isLastQuestion = currentQuestionIndex == questions.size() - 1;
        boolean allAnswered = userAnswers.size() == questions.size();

        if (isLastQuestion || allAnswered) {
            btnSubmit.setVisibility(View.VISIBLE);
            if (isLastQuestion) {
                btnNext.setVisibility(View.GONE);
            }
        } else {
            btnSubmit.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
        }
    }

    private void showSubmitDialog() {
        saveCurrentAnswer();

        int answeredCount = userAnswers.size();
        int totalCount = questions.size();

        String message = String.format(Locale.getDefault(),
                "You have answered %d out of %d questions.\n\nAre you sure you want to submit your quiz?",
                answeredCount, totalCount);

        new AlertDialog.Builder(this)
                .setTitle("Submit Quiz")
                .setMessage(message)
                .setPositiveButton("Submit", (dialog, which) -> submitQuiz())
                .setNegativeButton("Cancel", null)
                .show();
    }
    private List<AnswerRequest> createAnswerList() {
        List<AnswerRequest> answerList = new ArrayList<>();
        for (int i = 0; i < questions.size(); i++) {
            AnswerRequest answer = new AnswerRequest();
            answer.setQuestionId(questions.get(i).getId());
            answer.setSelectedAnswer(userAnswers.get(i));
            answer.setCorrect(userAnswers.get(i) != null && userAnswers.get(i).equals(questions.get(i).getCorrectAnswerLetter()));
            answerList.add(answer);
        }
        return answerList;
    }
    private void submitQuiz() {
        isQuizCompleted = true;
        saveCurrentAnswer();

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        // Xóa quiz state sau khi submit
        deleteCurrentQuizState();

        // Calculate score
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = userAnswers.get(i);
            if (userAnswer != null && userAnswer.equals(questions.get(i).getCorrectAnswerLetter())) {
                correctAnswers++;
            }
        }

        double score = questions.size() > 0 ? (double) correctAnswers / questions.size() * 100 : 0;
        long durationInMillis = QUIZ_DURATION - timeLeftInMillis;

        Intent resultIntent = new Intent(this, QuizResultActivity.class);
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("correct_answers", correctAnswers);
        resultIntent.putExtra("total_questions", questions.size());
        resultIntent.putExtra("quiz_title", quizTitle);
        resultIntent.putExtra("questions", new ArrayList<>(questions));
        resultIntent.putExtra("userAnswers", new HashMap<>(userAnswers));
        resultIntent.putExtra("duration", durationInMillis);
        List<AnswerRequest> answerList = createAnswerList();

        savePracticeHistory(
                examId,
                subjectCode,
                examTypeCode,
                currentUser.getId(),
                startTime,
                getCurrentTimeISO(),
                questions.size(),
                correctAnswers,
                score,
                (int) (durationInMillis / 1000),
                answerList
        );
        startActivity(resultIntent);
        finish();
    }

    private void deleteCurrentQuizState() {
        quizStateRepository.deleteQuizState(quizId, new QuizStateRepository.DeleteCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Quiz state deleted successfully");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error deleting quiz state: " + error);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showExitDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        showExitDialog();
    }

    private void showExitDialog() {
        String message = hasUnsavedChanges ?
                "Are you sure you want to exit? Your progress will be automatically saved and you can resume later." :
                "Are you sure you want to exit?";

        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage(message)
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (hasUnsavedChanges) {
                        saveQuizStateIfNeeded();
                    }
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private String getCurrentTimeISO() {
        return java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC).toString();
    }
    private String generateStateId() {
        return subjectCode + "_quiz_state_";
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Lưu state khi app bị pause
        saveQuizStateIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Kiểm tra network status khi resume
        checkNetworkStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Cleanup timers
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        if (autoSaveHandler != null) {
            autoSaveHandler.removeCallbacksAndMessages(null);
        }

        if (networkCheckHandler != null) {
            networkCheckHandler.removeCallbacksAndMessages(null);
        }

        // Unregister network receiver
        if (networkReceiver != null) {
            try {
                unregisterReceiver(networkReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Network receiver not registered", e);
            }
        }

        // Shutdown repository
        if (quizStateRepository != null) {
            quizStateRepository.shutdown();
        }

        // Lưu state cuối cùng trước khi destroy
        if (!isQuizCompleted && hasUnsavedChanges) {
            saveQuizStateIfNeeded();
        }
    }

    // Network change receiver
    private class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkNetworkStatus();
        }
    }
}