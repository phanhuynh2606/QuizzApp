package com.example.quizzapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.models.Quiz;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {
    private List<Quiz> quizzes;
    private OnQuizClickListener listener;

    public interface OnQuizClickListener {
        void onQuizClick(Quiz quiz);
    }

    public QuizAdapter(List<Quiz> quizzes, OnQuizClickListener listener) {
        this.quizzes = quizzes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizzes.get(position);
        holder.bind(quiz, listener);
    }

    @Override
    public int getItemCount() {
        return quizzes.size();
    }

    static class QuizViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvDescription, tvSubject, tvQuestions, tvExamType, tvDuration;
        private Button btnStartQuiz;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuizTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvQuestions = itemView.findViewById(R.id.tvQuestionCount);
            tvExamType = itemView.findViewById(R.id.tvExamLevel);
            btnStartQuiz = itemView.findViewById(R.id.btnStartQuiz);
            tvDuration = itemView.findViewById(R.id.tvDuration);

        }

        public void bind(Quiz quiz, OnQuizClickListener listener) {
            tvTitle.setText(quiz.getExamCode());
            tvDescription.setText(quiz.getDescription());
            tvSubject.setText(quiz.getSubjectCode());
            tvQuestions.setText(quiz.getTotalQuestions() + " questions");
            tvDuration.setText(quiz.getDuration() + " minutes");
            Log.d("QuizAdapter", "ExamType = " + quiz.getExamType());
            if (quiz.getExamType() != null) {
                tvExamType.setText(quiz.getExamType().getTypeName());
            } else {
                tvExamType.setText("Unknown");
            }

            btnStartQuiz.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuizClick(quiz);
                }
            });
        }
    }
}
