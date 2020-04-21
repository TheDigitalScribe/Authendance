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

public class AttendanceScreen extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private AttendanceAdapter attendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_recyclerview);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getStudents();
    }

    private void getStudents() {

        Intent intent = new Intent();
        //Bundle bundle = intent.getExtras();
        //assert bundle != null;
        //String module = bundle.getString("MOD_ID");
        //String date = bundle.getString("DATE_PICKED");

        //Log.d("ATT_SCREEN", module + date);

        /*CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(module)
                .collection("Date")
                .document(date)
                .collection("Students");

        final Query query = moduleRef.whereEqualTo("attended", true);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        queryDocumentSnapshot.getId();
                    }
                }
            }
        });

        FirestoreRecyclerOptions<Student> students = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query, Student.class)
                .build();

        attendAdapter = new AttendanceAdapter(students);

        RecyclerView recyclerView = findViewById(R.id.classRecyclerView);
        //recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AttendanceScreen.this));
        recyclerView.setAdapter(attendAdapter);
        attendAdapter.notifyDataSetChanged();*/
    }
}
