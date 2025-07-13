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
import com.example.quizzapp.adapters.SemesterAdapter;
import com.example.quizzapp.models.Semester;
import com.example.quizzapp.repository.SemesterRepository;

import java.util.ArrayList;
import java.util.List;


public class SemesterActivity extends AppCompatActivity {

    private SemesterRepository semesterRepository;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semester_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Semesters");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Semester> semesterList = new ArrayList<>();
        SemesterAdapter adapter = new SemesterAdapter(semesterList, semester -> {
            Intent intent = new Intent(SemesterActivity.this, SubjectActivity.class);
            intent.putExtra("semester_id", semester.getId());
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        semesterRepository = new SemesterRepository(this);

        semesterRepository.getAllSemesters(new SemesterRepository.SemesterCallback() {
            @Override
            public void onSuccess(List<Semester> semesters) {
                semesterList.clear();
                semesterList.addAll(semesters);
                runOnUiThread(adapter::notifyDataSetChanged);
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() ->
                        Toast.makeText(SemesterActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show()
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
