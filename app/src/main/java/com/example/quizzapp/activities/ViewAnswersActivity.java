package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.quizzapp.R;
import com.example.quizzapp.models.Question;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewAnswersActivity extends AppCompatActivity {

    private TextView tvQuestion, tvOptions, tvYourAnswer;
    private Button btnPrevious, btnNext, btnBackHome;

    private ArrayList<Question> questions;
    private HashMap<Integer, String> userAnswers;
    private int currentIndex = 0;

    private boolean isHistory = false;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_answers);

        tvQuestion = findViewById(R.id.tvQuestion);
        tvOptions = findViewById(R.id.tvOptions);
        tvYourAnswer = findViewById(R.id.tvYourAnswer);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnBackHome = findViewById(R.id.btnBackHome);

        questions = (ArrayList<Question>) getIntent().getSerializableExtra("questions");
        userAnswers = (HashMap<Integer, String>) getIntent().getSerializableExtra("userAnswers");
        isHistory = getIntent().getBooleanExtra("isHistory", false);
        userId = getIntent().getStringExtra("userId");
        displayQuestion();

        btnPrevious.setOnClickListener(v -> {
            if (currentIndex > 0) {
                currentIndex--;
                displayQuestion();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < questions.size() - 1) {
                currentIndex++;
                displayQuestion();
            }
        });

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(ViewAnswersActivity.this, isHistory ? HistoryActivity.class : DashboardActivity.class);
            intent.putExtra("userId", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void displayQuestion() {
        if (questions == null || userAnswers == null) return;
        Question q = questions.get(currentIndex);
        String userAnswer = userAnswers.get(currentIndex);
        String correctAnswer = q.getCorrectAnswerLetter();

        tvQuestion.setText("Q" + (currentIndex + 1) + ": " + q.getQuestionText());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < q.getOptions().size(); i++) {
            String letter = q.getOptions().get(i).getLetter();
            String text = q.getOptions().get(i).getText();

            sb.append(letter).append(") ").append(text);
            Log.d("letter", letter);
            if (letter.equals(correctAnswer)) {
                sb.append(" ✅");
            }

            if (letter.equals(userAnswer) && !letter.equals(correctAnswer)) {
                sb.append(" ❌");
            }
            sb.append("\n");
        }

        tvOptions.setText(sb.toString());

        if (userAnswer != null && userAnswer.equals(correctAnswer)) {
            tvYourAnswer.setText("Your answer: " + userAnswer + " ✅");
            tvYourAnswer.setTextColor(getResources().getColor(R.color.green, null));
        } else {
            tvYourAnswer.setText("Your answer: " + (userAnswer != null ? userAnswer : "No answer") + " ❌");
            tvYourAnswer.setTextColor(getResources().getColor(R.color.red, null));
        }

        btnPrevious.setEnabled(currentIndex > 0);
        btnNext.setEnabled(currentIndex < questions.size() - 1);
    }
}
