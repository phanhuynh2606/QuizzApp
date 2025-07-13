package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.example.quizzapp.models.Question;
import com.example.quizzapp.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizTakeActivity extends AppCompatActivity {
    private static final String TAG = "QuizTakeActivity";
    private static final long QUIZ_DURATION = 15 * 60 * 1000; // 15 phút

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

    // Data
    private QuestionRepository questionRepository;
    private List<Question> questions;
    private Map<Integer, String> userAnswers; // Map question index to selected answer
    private int currentQuestionIndex = 0;
    private String subjectCode;
    private String quizTitle;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = QUIZ_DURATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_take);

        initViews();
        setupToolbar();
        initData();
        loadQuestions();
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

    private void initData() {
        questionRepository = new QuestionRepository(this);
        questions = new ArrayList<>();
        userAnswers = new HashMap<>();

        // Get data from intent
        Intent intent = getIntent();
        subjectCode = intent.getStringExtra("subject_code");
        quizTitle = intent.getStringExtra("quiz_title");
        if (subjectCode == null) {
            Toast.makeText(this, "Subject code not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (quizTitle != null) {
            tvQuizTitle.setText(quizTitle);
        }
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

    private void setupQuiz() {
        // Start timer
        startTimer();

        // Display first question
        displayQuestion();

        // Update UI
        updateProgress();
        updateAnsweredCount();
        updateNavigationButtons();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerDisplay();
            }

            @Override
            public void onFinish() {
                // Time's up - auto submit
                Toast.makeText(QuizTakeActivity.this, "Time's up! Quiz submitted automatically.", Toast.LENGTH_SHORT).show();
                submitQuiz();
            }
        }.start();
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
            // Display question
            tvQuestion.setText(question.getQuestionText());
            String optionFormat = getString(R.string.option_format);

            // Display options
            rbOptionA.setText(String.format(optionFormat, "A", question.getOptions().get(0).getText()));
            rbOptionB.setText(String.format(optionFormat, "B", question.getOptions().get(1).getText()));
            rbOptionC.setText(String.format(optionFormat, "C", question.getOptions().get(2).getText()));
            rbOptionD.setText(String.format(optionFormat, "D", question.getOptions().get(3).getText()));

            // Restore saved answer if exists

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
            // Update question number
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
        }
    }

    private void previousQuestion() {
        if (currentQuestionIndex > 0) {
            saveCurrentAnswer();
            currentQuestionIndex--;
            displayQuestion();
            updateProgress();
            updateNavigationButtons();
        }
    }

    private void nextQuestion() {
        if (currentQuestionIndex < questions.size() - 1) {
            saveCurrentAnswer();
            currentQuestionIndex++;
            displayQuestion();
            updateProgress();
            updateNavigationButtons();
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
        // Previous button
        btnPrevious.setEnabled(currentQuestionIndex > 0);

        // Next button
        boolean hasNext = currentQuestionIndex < questions.size() - 1;
        btnNext.setEnabled(hasNext);

        // Submit button - show on last question or when all answered
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

    private void submitQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        // Calculate score
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            String userAnswer = userAnswers.get(i);
            if (userAnswer != null && userAnswer.equals(questions.get(i).getCorrectAnswerLetter())) {
                correctAnswers++;
            }
        }
        double score = questions.size() > 0 ? (double) correctAnswers / questions.size() * 100 : 0;
        Intent resultIntent = new Intent(this, QuizResultActivity.class);
        resultIntent.putExtra("score", score);
        resultIntent.putExtra("correct_answers", correctAnswers);
        resultIntent.putExtra("total_questions", questions.size());
        resultIntent.putExtra("quiz_title", quizTitle);
        resultIntent.putExtra("questions", new ArrayList<>(questions));
        resultIntent.putExtra("userAnswers", new HashMap<>(userAnswers));
        startActivity(resultIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showExitDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        showExitDialog();
//    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to exit? Your progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> {
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}