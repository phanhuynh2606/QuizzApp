package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.adapters.QuizAdapter;
import com.example.quizzapp.models.Quiz;
import com.example.quizzapp.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuizAdapter adapter;
    private QuizRepository repository;
    private List<Quiz> quizzes;

    private  String subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);

        initViews();
        setupRecyclerView();
        repository = new QuizRepository(this);
        loadQuizzes();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Quizzes");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setupRecyclerView() {
        quizzes = new ArrayList<>();
        adapter = new QuizAdapter(quizzes, this::onQuizClicked);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void loadQuizzes() {
        subjectId = getIntent().getStringExtra("subject_id");
        if (subjectId == null) {
            Toast.makeText(this, "Subject ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        repository.getQuizzesBySubject(subjectId, new QuizRepository.QuizListCallback() {
            @Override
            public void onSuccess(List<Quiz> quizList) {
                runOnUiThread(() -> {
                    quizzes.clear();
                    quizzes.addAll(quizList);
                    adapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(QuizListActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onQuizClicked(Quiz quiz) {
        Intent intent = new Intent(this, QuizTakeActivity.class);
        intent.putExtra("quiz_id", quiz.getId());
        intent.putExtra("quiz_title", quiz.getExamCode());
        intent.putExtra("subject_code", quiz.getSubjectCode());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.action_refresh) {
            loadQuizzes();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
