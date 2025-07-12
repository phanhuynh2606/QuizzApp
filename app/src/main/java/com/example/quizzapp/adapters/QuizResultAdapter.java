package com.example.quizzapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.models.UserAnswer;

import java.util.List;

public class QuizResultAdapter extends RecyclerView.Adapter<QuizResultAdapter.ResultViewHolder> {
    private List<UserAnswer> userAnswers;

    public QuizResultAdapter(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_answer_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        UserAnswer userAnswer = userAnswers.get(position);
        holder.bind(userAnswer, position + 1);
    }

    @Override
    public int getItemCount() {
        return userAnswers.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        private TextView tvQuestionNumber, tvQuestion;
        private LinearLayout llOptionA, llOptionB, llOptionC, llOptionD;
        private TextView tvOptionA, tvOptionB, tvOptionC, tvOptionD;
        private TextView tvStatusA, tvStatusB, tvStatusC, tvStatusD;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionNumber = itemView.findViewById(R.id.tvQuestionNumber);
            tvQuestion = itemView.findViewById(R.id.tvQuestion);

            // Option A
            llOptionA = itemView.findViewById(R.id.llOptionA);
            tvOptionA = itemView.findViewById(R.id.tvOptionA);
            tvStatusA = itemView.findViewById(R.id.tvStatusA);

            // Option B
            llOptionB = itemView.findViewById(R.id.llOptionB);
            tvOptionB = itemView.findViewById(R.id.tvOptionB);
            tvStatusB = itemView.findViewById(R.id.tvStatusB);

            // Option C
            llOptionC = itemView.findViewById(R.id.llOptionC);
            tvOptionC = itemView.findViewById(R.id.tvOptionC);
            tvStatusC = itemView.findViewById(R.id.tvStatusC);

            // Option D
            llOptionD = itemView.findViewById(R.id.llOptionD);
            tvOptionD = itemView.findViewById(R.id.tvOptionD);
            tvStatusD = itemView.findViewById(R.id.tvStatusD);
        }

        public void bind(UserAnswer userAnswer, int questionNumber) {
            tvQuestionNumber.setText("Câu " + questionNumber);
            tvQuestion.setText(userAnswer.getQuestionText());

            // Lấy thông tin từ Question model (cần thêm các field này vào UserAnswer hoặc lấy từ database)
            // Giả sử UserAnswer có các phương thức này:
            String optionA = getOptionText(userAnswer, "A");
            String optionB = getOptionText(userAnswer, "B");
            String optionC = getOptionText(userAnswer, "C");
            String optionD = getOptionText(userAnswer, "D");

            tvOptionA.setText(optionA);
            tvOptionB.setText(optionB);
            tvOptionC.setText(optionC);
            tvOptionD.setText(optionD);

            String correctAnswer = userAnswer.getCorrectAnswer();
            String userSelectedAnswer = userAnswer.getUserAnswer();

            // Reset tất cả các trạng thái
            resetOptionStyles();

            // Tô màu và hiển thị biểu tượng cho từng đáp án
            setupOption("A", llOptionA, tvStatusA, correctAnswer, userSelectedAnswer);
            setupOption("B", llOptionB, tvStatusB, correctAnswer, userSelectedAnswer);
            setupOption("C", llOptionC, tvStatusC, correctAnswer, userSelectedAnswer);
            setupOption("D", llOptionD, tvStatusD, correctAnswer, userSelectedAnswer);
        }

        private void resetOptionStyles() {
            // Reset về màu mặc định
            llOptionA.setBackgroundResource(R.drawable.bg_subject_tag);
            llOptionB.setBackgroundResource(R.drawable.bg_subject_tag);
            llOptionC.setBackgroundResource(R.drawable.bg_subject_tag);
            llOptionD.setBackgroundResource(R.drawable.bg_subject_tag);

            // Ẩn tất cả các biểu tượng
            tvStatusA.setVisibility(View.GONE);
            tvStatusB.setVisibility(View.GONE);
            tvStatusC.setVisibility(View.GONE);
            tvStatusD.setVisibility(View.GONE);
        }

        private void setupOption(String option, LinearLayout layout, TextView statusView,
                               String correctAnswer, String userAnswer) {
            boolean isCorrectAnswer = option.equals(correctAnswer);
            boolean isUserChoice = option.equals(userAnswer);

            if (isCorrectAnswer) {
                // Đáp án đúng -> tô màu xanh
                layout.setBackgroundResource(R.drawable.bg_correct_answer);

                if (isUserChoice) {
                    // Người dùng chọn đúng -> hiển thị dấu ✓
                    statusView.setText("✓");
                    statusView.setVisibility(View.VISIBLE);
                }
            } else if (isUserChoice) {
                // Người dùng chọn sai -> tô màu đỏ và hiển thị dấu ✗
                layout.setBackgroundResource(R.drawable.bg_wrong_answer);
                statusView.setText("✗");
                statusView.setVisibility(View.VISIBLE);
            }
            // Các đáp án khác giữ màu mặc định (bg_subject_tag)
        }

        private String getOptionText(UserAnswer userAnswer, String option) {
            // Sử dụng các getter methods từ UserAnswer model
            switch (option) {
                case "A": return userAnswer.getOptionA() != null ? userAnswer.getOptionA() : "Option A";
                case "B": return userAnswer.getOptionB() != null ? userAnswer.getOptionB() : "Option B";
                case "C": return userAnswer.getOptionC() != null ? userAnswer.getOptionC() : "Option C";
                case "D": return userAnswer.getOptionD() != null ? userAnswer.getOptionD() : "Option D";
                default: return "";
            }
        }
    }
}
