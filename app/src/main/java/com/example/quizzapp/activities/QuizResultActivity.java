package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzapp.R;
import com.example.quizzapp.models.Question;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvResultTitle, tvQuizTitle, tvScore, tvCorrectAnswers, tvTotalPoints;
    private TextView tvDuration, tvTotalQuestions, tvAccuracy;
    private ProgressBar progressBar;
    private Button btnViewAnswers, btnRetakeQuiz, btnBackToQuizList;
    private ArrayList<Question> questions;
    private HashMap<Integer, String> userAnswers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        initViews();
        loadData();
    }

    private void initViews() {
        tvResultTitle = findViewById(R.id.tvResultTitle);
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvScore = findViewById(R.id.tvScore);
        tvCorrectAnswers = findViewById(R.id.tvCorrectAnswers);
        tvTotalPoints = findViewById(R.id.tvTotalPoints);
        tvDuration = findViewById(R.id.tvDuration);
        tvTotalQuestions = findViewById(R.id.tvTotalQuestions);
        tvAccuracy = findViewById(R.id.tvAccuracy);
        progressBar = findViewById(R.id.progressBar);
        btnViewAnswers = findViewById(R.id.btnViewAnswers);
        btnRetakeQuiz = findViewById(R.id.btnRetakeQuiz);
        btnBackToQuizList = findViewById(R.id.btnBackToQuizList);
    }

    private void loadData() {
        Intent intent = getIntent();
        double score = intent.getDoubleExtra("score", 0);
        int correctAnswers = intent.getIntExtra("correct_answers", 0);
        int totalQuestions = intent.getIntExtra("total_questions", 0);
        String quizTitle = intent.getStringExtra("quiz_title");
        questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        userAnswers= (HashMap<Integer, String>) getIntent().getSerializableExtra("userAnswers");        // Set quiz title
        tvQuizTitle.setText(quizTitle != null ? quizTitle : "Quiz");

        // Set score
        String scoreText = String.format("%.0f%%", score);
        tvScore.setText(scoreText);
        progressBar.setProgress((int) score);

        // Correct answers
        tvCorrectAnswers.setText(String.format("Correct: %d/%d", correctAnswers, totalQuestions));

        // Total points (fake points example: score out of 100)
        int pointScore = (int) score;
        tvTotalPoints.setText(String.format("Points: %d/100", pointScore));

        // Duration (fake value for now, you can pass actual duration if needed)
        tvDuration.setText("15:00");

        // Total questions
        tvTotalQuestions.setText(String.valueOf(totalQuestions));

        // Accuracy
        String accuracyText = String.format("%.0f%%", score);
        tvAccuracy.setText(accuracyText);

        // Button actions
        btnViewAnswers.setOnClickListener(v -> {

        });

        btnRetakeQuiz.setOnClickListener(v -> {
            finish();
        });

        btnBackToQuizList.setOnClickListener(v -> {
            // Return to quiz list (main activity)
            Intent backIntent = new Intent(QuizResultActivity.this, QuizListActivity.class);
            backIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(backIntent);
            finish();
        });
    }
}
