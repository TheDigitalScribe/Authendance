package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class StudentModules extends AppCompatActivity {
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

    private void getModules() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        final CollectionReference studentRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        Query query = studentRef.orderBy("name", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<StudentModuleItem> modules = new FirestoreRecyclerOptions.Builder<StudentModuleItem>()
                .setQuery(query, StudentModuleItem.class)
                .build();

        moduleAdapter = new StudentModuleAdapter(modules);
        RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentModules.this));
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
