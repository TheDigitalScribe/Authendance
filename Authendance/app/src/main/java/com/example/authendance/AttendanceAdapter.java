package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AttendanceAdapter extends FirestoreRecyclerAdapter<Student, AttendanceAdapter.AttendanceHolder> {


    public AttendanceAdapter(@NonNull FirestoreRecyclerOptions<Student> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AttendanceHolder holder, int position, @NonNull Student model) {
        holder.studentID.setText(model.getStudentID());
        holder.checkerImg.setImageResource(model.getImageResource());
    }

    @NonNull
    @Override
    public AttendanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_layout, parent, false);
        return new AttendanceHolder(v);
    }

    class AttendanceHolder extends RecyclerView.ViewHolder {

        TextView studentID;
        ImageView checkerImg;

        public AttendanceHolder(@NonNull View itemView) {
            super(itemView);
            studentID = itemView.findViewById(R.id.studentID);
            checkerImg = itemView.findViewById(R.id.checker);
        }
    }
}
