package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class TeacherUserAdapter extends FirestoreRecyclerAdapter<TeacherUsers, TeacherUserAdapter.TeacherHolder> {

    public TeacherUserAdapter(@NonNull FirestoreRecyclerOptions options) {
        super(options);
    }


    @NonNull
    @Override
    public TeacherHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_user_rv_layout, parent, false);
        return new TeacherHolder(v);

    }

    @Override
    protected void onBindViewHolder(@NonNull TeacherHolder holder, int position, @NonNull TeacherUsers model) {

        holder.teacherID.setText(model.getTeacher_id());
        holder.teacherName.setText(model.getName());

    }

    class TeacherHolder extends RecyclerView.ViewHolder {

        TextView teacherID;
        TextView teacherName;

        public TeacherHolder(@NonNull View itemView) {
            super(itemView);

            teacherID = itemView.findViewById(R.id.teacherIDText);
            teacherName = itemView.findViewById(R.id.teacherNameText);
        }
    }
}
