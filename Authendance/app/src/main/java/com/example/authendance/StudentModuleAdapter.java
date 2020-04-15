package com.example.authendance;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class StudentModuleAdapter extends FirestoreRecyclerAdapter<StudentModuleItem, StudentModuleAdapter.ModuleHolder> {

    public StudentModuleAdapter(@NonNull FirestoreRecyclerOptions<StudentModuleItem> options) {
        super(options);
    }


    //Tells the adapter what to put in each view in the Card layout
    @Override
    protected void onBindViewHolder(@NonNull ModuleHolder holder, int position, @NonNull StudentModuleItem model) {
        holder.name.setText(model.getName());
    }

    @NonNull
    @Override
    public ModuleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_module, parent, false);
        return new ModuleHolder(v);
    }

    class ModuleHolder extends RecyclerView.ViewHolder {
        TextView name;

        public ModuleHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.moduleName);
        }
    }
}
