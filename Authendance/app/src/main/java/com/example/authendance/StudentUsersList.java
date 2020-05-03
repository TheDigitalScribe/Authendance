package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
        Toolbar toolbar = findViewById(R.id.toolbarStudents);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = findViewById(R.id.fab);

        getStudents();

        if(!isConnectedtoInternet(StudentUsersList.this)){
            Toast.makeText(StudentUsersList.this, "Please connect to internet to see list", Toast.LENGTH_SHORT).show();
        }


        //Shows student's overall attendance when their ID is clicked if connected to internet
        adapter.setOnItemClickListener(new StudentUserAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int position) {

                if (isConnectedtoInternet(StudentUsersList.this)) {
                    String studentID = documentSnapshot.getString("student_id");

                    Intent intent = new Intent(StudentUsersList.this, StudentAttendanceScreen.class);
                    intent.putExtra("STU_ID", studentID);
                    startActivity(intent);
                } else {
                    Toast.makeText(StudentUsersList.this, "Please connect to internet to see student attendance", Toast.LENGTH_LONG).show();
                }
            }

        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(StudentUsersList.this);
                builder.setTitle("See Attendance");
                builder.setMessage("Click student to see their overall attendance.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

    private void getStudents() {

        //Determines document path for the records of students
        CollectionReference userRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User");

        //Retrieves student IDs
        Query query = userRef.orderBy("student_id", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    Objects.requireNonNull(task.getResult());

                } else {
                    Toast.makeText(StudentUsersList.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
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

    //Checks if user is connected to the internet
    public static boolean isConnectedtoInternet(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Toast.makeText(context, "You're not connected to the internet", Toast.LENGTH_SHORT).show();
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(isConnectedtoInternet(StudentUsersList.this))
        {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
