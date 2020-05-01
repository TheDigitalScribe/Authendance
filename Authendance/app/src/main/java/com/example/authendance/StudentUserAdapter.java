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


public class StudentUserAdapter extends FirestoreRecyclerAdapter<StudentUsers, StudentUserAdapter.StudentHolder> {

    private OnItemClickListener listener;

    public StudentUserAdapter(@NonNull FirestoreRecyclerOptions<StudentUsers> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentHolder holder, int position, @NonNull StudentUsers model) {

        holder.studentID.setText(model.getStudent_id());
        holder.studentName.setText(model.getName());

    }

    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_user_rv_layout, parent, false);
        return new StudentHolder(v);

    }

    class StudentHolder extends RecyclerView.ViewHolder {

        TextView studentID;
        TextView studentName;

        public StudentHolder(@NonNull View itemView) {
            super(itemView);

            studentID = itemView.findViewById(R.id.studentIDText);
            studentName = itemView.findViewById(R.id.studentNameText);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {

        this.listener = listener;

    }
}
