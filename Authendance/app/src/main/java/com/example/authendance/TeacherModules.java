package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class TeacherModules extends AppCompatActivity {

    private StudentModuleAdapter moduleAdapter;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_modules);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getModules();
    }

    public void getModules() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        final CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        Query query = moduleRef.orderBy("module", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                        queryDocumentSnapshot.getId();
                    }
                    moduleAdapter.notifyDataSetChanged();
                }
            }
        });

        FirestoreRecyclerOptions<StudentModuleItem> modules = new FirestoreRecyclerOptions.Builder<StudentModuleItem>()
                .setQuery(query, StudentModuleItem.class)
                .build();

        moduleAdapter = new StudentModuleAdapter(modules);
        RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TeacherModules.this));
        recyclerView.setAdapter(moduleAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        moduleAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        moduleAdapter.stopListening();
    }
}
