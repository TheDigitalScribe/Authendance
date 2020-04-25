//This class is for retrieving the student ID's from Firestore and storing it in a RecyclerView

package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class AttendanceAdapter extends FirestoreRecyclerAdapter<Student, AttendanceAdapter.StudentHolder> {

    private OnItemLongClickListener listener;

    public AttendanceAdapter(@NonNull FirestoreRecyclerOptions<Student> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentHolder holder, int position, @NonNull Student model) {

        holder.studentTV.setText(model.getStudent_id());
    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.attendance_rv_layout,
                parent, false);

        return new StudentHolder(v);
    }

    class StudentHolder extends RecyclerView.ViewHolder {

        TextView studentTV;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);
            studentTV = itemView.findViewById(R.id.studentTV);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });

        }
    }

    public interface OnItemLongClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.listener = listener;
    }
}
