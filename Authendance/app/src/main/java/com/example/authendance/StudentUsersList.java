package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class StudentUsersList extends AppCompatActivity {

    private StudentUserAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_user_rv);

        db = FirebaseFirestore.getInstance();

        getStudents();
    }

    private void getStudents() {

       CollectionReference userRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User");

       Query query = userRef.orderBy("student_id", Query.Direction.DESCENDING);
       query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        queryDocumentSnapshot.getString("student_id");
                    }

                }
                else {
                    Toast.makeText(StudentUsersList.this, "Error: " + task.getException(), Toast.LENGTH_LONG).show();
                }
           }
       });

        FirestoreRecyclerOptions<StudentUsers> students = new FirestoreRecyclerOptions.Builder<StudentUsers>()
                .setQuery(query, StudentUsers.class)
                .build();


        adapter = new StudentUserAdapter(students);
        RecyclerView recyclerView = findViewById(R.id.studentUserRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(StudentUsersList.this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
