package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PersonalAttendanceAdapter extends FirestoreRecyclerAdapter<Date, PersonalAttendanceAdapter.DateHolder> {


    public PersonalAttendanceAdapter(@NonNull FirestoreRecyclerOptions<Date> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DateHolder holder, int position, @NonNull Date model) {
        holder.dateText.setText(model.getDate());
    }

    @NonNull
    @Override
    public DateHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_attendance_rv_layout, parent, false);
        return new DateHolder(v);
    }

    class DateHolder extends RecyclerView.ViewHolder {

        TextView dateText;

        public DateHolder(@NonNull View itemView) {
            super(itemView);

            dateText = itemView.findViewById(R.id.dateText);
        }
    }
}
