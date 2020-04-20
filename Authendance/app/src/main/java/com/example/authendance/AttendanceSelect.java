package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AttendanceSelect extends AppCompatActivity {

    private Spinner attendanceSpinner;
    private FirebaseFirestore db;

    private Button submitBtn;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_select);

        attendanceSpinner = findViewById(R.id.attendanceSpinner);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        uid = fAuth.getCurrentUser().getUid();

        CalendarView calendarView = findViewById(R.id.calendarView);
        submitBtn = findViewById(R.id.submitBtn);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                month = month + 1;
                final String datePicked = dayOfMonth + " " + "0" + month + " " + year;

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (datePicked.isEmpty()) {
                            Toast.makeText(AttendanceSelect.this, "Please enter date", Toast.LENGTH_SHORT).show();

                        } else {

                            submitDate(datePicked);

                        }
                    }
                });
            }
        });

        populateSpinner();
    }

    private void populateSpinner() {

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        //Prepares spinner
        final List<String> modulesList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.custom_spinner, modulesList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        attendanceSpinner.setAdapter(adapter);

        //Searches for modules which has the corresponding teacher ID and adds them to spinner
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {
                        String moduleName = queryDocumentSnapshot.getId();
                        modulesList.add(moduleName);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void submitDate(final String datePicked) {
        final String spinnerValue = attendanceSpinner.getSelectedItem().toString();

        DocumentReference dateRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(spinnerValue)
                .collection("Date")
                .document(datePicked);

        dateRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists()) {
                        Toast.makeText(AttendanceSelect.this, "Doc exists", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(AttendanceSelect.this, "No attendance record for this module on " + datePicked , Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
