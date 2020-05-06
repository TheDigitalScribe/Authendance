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

public class ModuleAdapter extends FirestoreRecyclerAdapter<StudentModuleItem, ModuleAdapter.ModuleHolder> {

    private OnItemClickListener listener;

    public ModuleAdapter(@NonNull FirestoreRecyclerOptions<StudentModuleItem> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ModuleHolder holder, int position, @NonNull StudentModuleItem model) {
        holder.name.setText(model.getModule());
        holder.lecturer_name.setText(model.getModule_lecturer());
        holder.date.setText(model.getModule_date());
    }

    @NonNull
    @Override
    public ModuleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.modules_rv_layout, parent, false);
        return new ModuleHolder(v);
    }

    class ModuleHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lecturer_name;
        TextView date;

        public ModuleHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.moduleName);
            lecturer_name = itemView.findViewById(R.id.module_lecturer_name);
            date = itemView.findViewById(R.id.lecture_date);

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

    public void setOnItemClickListener(ModuleAdapter.OnItemClickListener listener) {

        this.listener = listener;

    }
}
