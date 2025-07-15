package com.example.quizzapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quizzapp.R;
import com.example.quizzapp.models.PracticeHistory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private List<PracticeHistory> examHistories;
    private OnItemClickListener listener;
    private Context context;

    public interface OnItemClickListener {
        void onItemClick(PracticeHistory examHistory);
    }

    public HistoryAdapter(List<PracticeHistory> examHistories, OnItemClickListener listener) {
        this.examHistories = examHistories;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_exam_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PracticeHistory examHistory = examHistories.get(position);
        holder.bind(examHistory);
    }

    @Override
    public int getItemCount() {
        return examHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textSubjectCode;
        private TextView textExamType;
        private TextView textScore;
        private TextView textCorrectAnswers;
        private TextView textTotalQuestions;
        private TextView textTimeSpent;
        private TextView textDate;
        private View statusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textSubjectCode = itemView.findViewById(R.id.textSubjectCode);
            textExamType = itemView.findViewById(R.id.textExamType);
            textScore = itemView.findViewById(R.id.textScore);
            textCorrectAnswers = itemView.findViewById(R.id.textCorrectAnswers);
            textTotalQuestions = itemView.findViewById(R.id.textTotalQuestions);
            textTimeSpent = itemView.findViewById(R.id.textTimeSpent);
            textDate = itemView.findViewById(R.id.textDate);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(examHistories.get(position));
                    }
                }
            });
        }

        public void bind(PracticeHistory examHistory) {
            textSubjectCode.setText(examHistory.getSubjectCode());
            textExamType.setText(examHistory.getExamTypeCode());

            textScore.setText(String.format("%.0f%%", examHistory.getScore()));
            textCorrectAnswers.setText(String.valueOf(examHistory.getCorrectAnswers()));
            textTotalQuestions.setText(String.valueOf(examHistory.getTotalQuestions()));

            // Format time spent (convert seconds to MM:SS format)
            textTimeSpent.setText(formatTimeSpent(examHistory.getTimeSpent()));

            // Format date with start time
            textDate.setText(formatDateWithTime(examHistory.getStartTime()));

            // Set score color based on performance
            int scoreColor = getScoreColor(examHistory.getScore());
            textScore.setTextColor(context.getResources().getColor(scoreColor, context.getTheme()));

            // Set exam type badge background
            int examTypeBg = R.drawable.bg_exam_type_badge;
            textExamType.setBackgroundResource(examTypeBg);

            // Set status indicator color
            int statusColor = getStatusColor(examHistory.getScore());
            statusIndicator.setBackgroundColor(context.getResources().getColor(statusColor, context.getTheme()));
        }

        private String formatTimeSpent(long timeSpentInSeconds) {
            long hours = timeSpentInSeconds / 3600;
            long minutes = (timeSpentInSeconds % 3600) / 60;
            long seconds = timeSpentInSeconds % 60;

            if (hours > 0) {
                return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format(Locale.US, "%02d:%02d", minutes, seconds);
            }
        }

        private String formatDateWithTime(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Parse as UTC

                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.US);
                // Use local timezone for display (Vietnam time)
                dateFormat.setTimeZone(java.util.TimeZone.getDefault());
                timeFormat.setTimeZone(java.util.TimeZone.getDefault());

                Date date = inputFormat.parse(dateString);

                String formattedDate = dateFormat.format(date);
                String formattedTime = timeFormat.format(date);

                return formattedDate + " at " + formattedTime;
            } catch (Exception e) {
                // Fallback to original format if parsing fails
                return formatDate(dateString);
            }
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC")); // Parse as UTC

                SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                outputFormat.setTimeZone(java.util.TimeZone.getDefault()); // Display in local time

                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date);
            } catch (Exception e) {
                return dateString;
            }
        }

        private int getScoreColor(double score) {
            if (score >= 50) {
                return R.color.green;
            } else {
                return R.color.red;
            }
        }

        private int getStatusColor(double score) {
            if (score >= 50) {
                return R.color.green;
            } else {
                return R.color.red;
            }
        }
    }
}