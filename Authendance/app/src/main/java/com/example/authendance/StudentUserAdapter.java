package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class StudentUserAdapter extends FirestoreRecyclerAdapter<StudentUsers, StudentUserAdapter.StudentHolder> {

    public StudentUserAdapter(@NonNull FirestoreRecyclerOptions<StudentUsers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentHolder holder, int position, @NonNull StudentUsers model) {

        holder.studentID.setText(model.getStudent_id());

    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_user_rv_layout, parent, false);
        return new StudentHolder(v);

    }

    class StudentHolder extends RecyclerView.ViewHolder {

        TextView studentID;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);

            studentID = itemView.findViewById(R.id.studentIDText);
        }
    }
}
