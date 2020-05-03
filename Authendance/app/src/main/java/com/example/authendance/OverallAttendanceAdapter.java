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

public class OverallAttendanceAdapter extends FirestoreRecyclerAdapter<AdminAttendance, OverallAttendanceAdapter.AttendanceHolder> {

    private OnItemLongClickListener longListener;

    public OverallAttendanceAdapter(@NonNull FirestoreRecyclerOptions<AdminAttendance> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AttendanceHolder holder, int position, @NonNull AdminAttendance model) {

        holder.attendanceText.setText(model.getDate());
        holder.attendanceNum.setText(String.valueOf(model.getAttendance()));

    }

    @NonNull
    @Override
    public AttendanceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.overall_attendance_rv_layout, parent, false);
        return new AttendanceHolder(v);
    }

    class AttendanceHolder extends RecyclerView.ViewHolder {

        TextView attendanceText;
        TextView attendanceNum;

        public AttendanceHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int longPosition = getAdapterPosition();

                    if(longPosition != RecyclerView.NO_POSITION && longListener != null) {
                        longListener.onItemLongClick(getSnapshots().getSnapshot(longPosition), longPosition);
                        return true;
                    }
                    else {
                        return false;
                    }
                }
            });

            attendanceText = itemView.findViewById(R.id.attendanceText);
            attendanceNum = itemView.findViewById(R.id.attendanceNum);
        }


    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(OverallAttendanceAdapter.OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }
}
