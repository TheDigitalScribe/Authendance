package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ModuleAdapter extends FirestoreRecyclerAdapter<StudentModuleItem, ModuleAdapter.ModuleHolder> {
    public interface ActionCallback {
        void onLongClickListener();
    }
    private ActionCallback mActionCallbacks;

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

    class ModuleHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{
        TextView name;
        TextView lecturer_name;
        TextView date;

        public ModuleHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.moduleName);
            lecturer_name = itemView.findViewById(R.id.module_lecturer_name);
            date = itemView.findViewById(R.id.lecture_date);
        }

        @Override
        public boolean onLongClick(View v) {
            if (mActionCallbacks != null) {
                mActionCallbacks.onLongClickListener();
            }
            return true;
        }
    }

    public void addActionCallback(ActionCallback actionCallbacks) {
        mActionCallbacks = actionCallbacks;
    }
}
