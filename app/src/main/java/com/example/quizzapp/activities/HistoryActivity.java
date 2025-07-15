package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.adapters.HistoryAdapter;
import com.example.quizzapp.models.AnswerRequest;
import com.example.quizzapp.models.PracticeHistory;
import com.example.quizzapp.models.Question;
import com.example.quizzapp.models.User;
import com.example.quizzapp.repository.AuthRepository;
import com.example.quizzapp.repository.HistoryRepository;
import com.example.quizzapp.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayout emptyStateLayout;
    private HistoryAdapter adapter;
    private HistoryRepository repository;
    private String userId;
    private List<PracticeHistory> examHistories;
    private QuestionRepository questionRepository;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_history);
        initViews();
        setupRecyclerView();
        repository = new HistoryRepository(this);
        questionRepository = new QuestionRepository(this);
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        loadExamHistory();

    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Exam History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void setupRecyclerView() {
        examHistories = new ArrayList<>();
        adapter = new HistoryAdapter(examHistories, this::onExamHistoryClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
    private void loadExamHistory() {
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        repository.getExamHistory(userId, new HistoryRepository.ExamHistoryCallback() {
            @Override
            public void onSuccess(List<PracticeHistory> historyList) {
                runOnUiThread(() -> {
                    examHistories.clear();
                    examHistories.addAll(historyList);
                    adapter.notifyDataSetChanged();

                    // Show/hide empty state
                    if (examHistories.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        emptyStateLayout.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyStateLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(HistoryActivity.this, error, Toast.LENGTH_SHORT).show();
                    recyclerView.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                });
            }
        });
    }
    private HashMap<Integer, String> extractUserAnswers(PracticeHistory examHistory, List<Question> questions) {
        HashMap<Integer, String> userAnswers = new HashMap<>();

        // Get the answers array from exam history
        List<AnswerRequest> answers = examHistory.getAnswers();

        if (answers != null && !answers.isEmpty()) {
            // Create a map of question ID to answer for quick lookup
            HashMap<String, String> questionIdToAnswer = new HashMap<>();
            for (AnswerRequest answer : answers) {
                questionIdToAnswer.put(answer.getQuestionId(), answer.getSelectedAnswer());
            }
            // Map answers to question indices
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                String userAnswer = questionIdToAnswer.get(question.getId());
                if (userAnswer != null) {
                    userAnswers.put(i, userAnswer); // Use question index as key
                }
            }
        }

        return userAnswers;
    }
    private void onExamHistoryClicked(PracticeHistory examHistory) {
        String subjectCode = examHistory.getSubjectCode();

        questionRepository.getQuestionsBySubject(subjectCode, new QuestionRepository.QuestionListCallback() {
            @Override
            public void onSuccess(List<Question> questions) {
                HashMap<Integer, String> userAnswers = extractUserAnswers(examHistory, questions);
                Intent viewIntent = new Intent(HistoryActivity.this, ViewAnswersActivity.class);
                viewIntent.putExtra("questions", new ArrayList<>(questions));
                viewIntent.putExtra("userAnswers", userAnswers);
                viewIntent.putExtra("isHistory", true);
                viewIntent.putExtra("userId", userId);

                startActivity(viewIntent);
            }
            @Override
            public void onError(String message) {
                Toast.makeText(HistoryActivity.this, "Error loading questions: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            loadExamHistory();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExamHistory();
    }
}