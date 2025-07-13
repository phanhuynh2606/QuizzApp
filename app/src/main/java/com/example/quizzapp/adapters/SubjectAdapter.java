package com.example.quizzapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.quizzapp.R;
import com.example.quizzapp.models.Subject;
import java.util.List;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder> {
    private List<Subject> subjectList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Subject subject);
    }

    public SubjectAdapter(List<Subject> subjectList, OnItemClickListener listener) {
        this.subjectList = subjectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SubjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subject, parent, false);
        return new SubjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectViewHolder holder, int position) {
        Subject subject = subjectList.get(position);
        holder.tvSubjectCode.setText(subject.getSubjectCode());
        holder.tvSubjectName.setText(subject.getSubjectName());
        holder.tvSemester.setText("Semester: " + subject.getSemester().getName());
        holder.tvDescription.setText(subject.getDescription());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(subject);
            }
        });
    }

    @Override
    public int getItemCount() {
        return subjectList != null ? subjectList.size() : 0;
    }

    public static class SubjectViewHolder extends RecyclerView.ViewHolder {
        TextView tvSubjectCode, tvSubjectName, tvSemester, tvDescription;
        public SubjectViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSubjectCode = itemView.findViewById(R.id.tvSubjectCode);
            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            tvSemester = itemView.findViewById(R.id.tvSemester);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }
    }
}

