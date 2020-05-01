package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class OverallAttendance extends AppCompatActivity {

    private FirebaseFirestore db;
    private OverallAttendanceAdapter adapter;

    private String module;
    TextView toolbarText;
    Toolbar toolbar;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overall_attendance_rv);

        db = FirebaseFirestore.getInstance();

        //Sets up toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbarText = toolbar.findViewById(R.id.personalToolbarTV);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);

        Intent intent = getIntent();
        module = intent.getStringExtra("MOD_ID");
        Log.d("ATT_STUFF", "Module: " + module);

        toolbarText.setText(module);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        getAttendance();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OverallAttendance.this);
                builder.setTitle("Delete Attendance");
                builder.setMessage("Hold down on a date record to delete the attendance.");
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

    private void getAttendance() {

        CollectionReference dateRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(module)
                .collection("Date");

        Query query = dateRef.orderBy("date", Query.Direction.DESCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        queryDocumentSnapshot.getId();

                        Log.d("ATT_STUFF", queryDocumentSnapshot.getId());

                    }
                }
            }
        });

        FirestoreRecyclerOptions<AdminAttendance> attendance = new FirestoreRecyclerOptions.Builder<AdminAttendance>()
                .setQuery(query, AdminAttendance.class)
                .build();


        adapter = new OverallAttendanceAdapter(attendance);
        RecyclerView recyclerView = findViewById(R.id.overallAttRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(OverallAttendance.this));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemLongClickListener(new OverallAttendanceAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentSnapshot documentSnapshot, int position) {

                final String dateID = documentSnapshot.getId();

                AlertDialog.Builder builder = new AlertDialog.Builder(OverallAttendance.this);
                builder.setTitle("Delete Attendance Record?");
                builder.setMessage("Are you sure you want to delete this attendance record");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Attendance")
                                .document(module)
                                .collection("Date")
                                .document(dateID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(OverallAttendance.this, "Record: " + dateID + " successfully deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(OverallAttendance.this, "Record: " + dateID + " failed to delete", Toast.LENGTH_SHORT).show();
                                    }
                                });

                        CollectionReference studentRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Modules")
                                .document(module)
                                .collection("Students");

                        studentRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {

                                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                                        String studentID = queryDocumentSnapshot.getId();

                                        String docName = module.replaceAll("\\s+", "") + dateID.replaceAll("\\s+", "");
                                        Log.d("ATT_STUFF", "docname: " + docName);

                                        db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("AttendanceRecord")
                                                .document(studentID)
                                                .collection("Records")
                                                .document(docName)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d("ATT_STUFF", "Record deleted");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("ATT_STUFF", "Record not deleted");
                                                    }
                                                });
                                    }
                                }
                                else {
                                    Toast.makeText(OverallAttendance.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
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
