package com.example.quizzapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        private TextView tvTitle, tvDescription, tvSubject, tvQuestions, tvDifficulty;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvQuizTitle); // Fixed: use correct ID
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubject = itemView.findViewById(R.id.tvSubject);
            tvQuestions = itemView.findViewById(R.id.tvQuestionCount); // Fixed: use correct ID
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }

        public void bind(Quiz quiz, OnQuizClickListener listener) {
            tvTitle.setText(quiz.getTitle());
            tvDescription.setText(quiz.getDescription());
            tvSubject.setText(quiz.getSubject());
            tvQuestions.setText(quiz.getTotalQuestions() + " questions");
            tvDifficulty.setText(quiz.getDifficulty());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onQuizClick(quiz);
                }
            });
        }
    }
}
