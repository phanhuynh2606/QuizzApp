package com.example.quizzapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizzapp.R;
import com.example.quizzapp.models.Semester;
import java.util.List;

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder> {

    private final List<Semester> semesterList;
    private final OnSemesterClickListener listener;

    public interface OnSemesterClickListener {
        void onSemesterClick(Semester semester);
    }

    public SemesterAdapter(List<Semester> semesterList, OnSemesterClickListener listener) {
        this.semesterList = semesterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SemesterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_semester, parent, false);
        return new SemesterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterViewHolder holder, int position) {
        Semester semester = semesterList.get(position);
        holder.tvSemesterName.setText(semester.getName());
        holder.tvSemesterDescription.setText(semester.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSemesterClick(semester);
            }
        });
    }

    @Override
    public int getItemCount() {
        return semesterList != null ? semesterList.size() : 0;
    }

    public static class SemesterViewHolder extends RecyclerView.ViewHolder {
        TextView tvSemesterName, tvSemesterDescription;

        public SemesterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSemesterName = itemView.findViewById(R.id.tvSemesterName);
            tvSemesterDescription = itemView.findViewById(R.id.tvSemesterDescription);
        }
    }
}
