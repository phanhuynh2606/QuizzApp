package com.example.quizzapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.adapters.SubjectAdapter;
import com.example.quizzapp.models.Subject;
import com.example.quizzapp.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;

public class SubjectActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList = new ArrayList<>();
    private SubjectRepository subjectRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Subjects");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        subjectAdapter = new SubjectAdapter(subjectList, subject -> {
            Intent intent = new Intent(SubjectActivity.this, QuizListActivity.class);
            intent.putExtra("subject_id", subject.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(subjectAdapter);

        String semesterId = getIntent().getStringExtra("semester_id");
        if (semesterId == null) {
            Toast.makeText(this, "Semester ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        subjectRepository = new SubjectRepository(this);
        subjectRepository.getSubjectsBySemesterId(semesterId, new SubjectRepository.SubjectCallback() {
            @Override
            public void onSuccess(List<Subject> subjects) {
                subjectList.clear();
                subjectList.addAll(subjects);
                runOnUiThread(() -> subjectAdapter.notifyDataSetChanged());
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(SubjectActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
