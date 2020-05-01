package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class StudentAttendanceScreen extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;
    private String uid;

    private TextView totalAttTV;
    private TextView lecAttTV;
    private TextView lecMissedTV;
    private TextView percentageTV;
    private ProgressBar attendProgress;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_attendance_screen);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        totalAttTV = findViewById(R.id.totalAttTV);
        lecAttTV = findViewById(R.id.lecAttTV);
        lecMissedTV = findViewById(R.id.lecMissedTV);
        percentageTV = findViewById(R.id.percentageTV);
        attendProgress = findViewById(R.id.attendProgress);
        spinner = findViewById(R.id.spinner);
        Button submitBtn = findViewById(R.id.submitBtn);

        getRecord();
        populateSpinner();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uid = fAuth.getCurrentUser().getUid();
                final String spinnerValue = spinner.getSelectedItem().toString();

                //Determines database path for the user's document
                DocumentReference documentReference = db.collection("School")
                        .document("0DKXnQhueh18DH7TSjsb")
                        .collection("User")
                        .document(uid);

                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();

                            assert documentSnapshot != null;
                            if (documentSnapshot.exists()) {

                                //Retrieves student ID
                                String studentID = documentSnapshot.getString("student_id");
                                assert studentID != null;
                                Log.d("STU_ATT", studentID + " " + spinnerValue);

                                //Passes spinner value and student ID to the PersonalAttendance class
                                Bundle bundle = new Bundle();
                                bundle.putString("MOD_ID", spinnerValue);
                                bundle.putString("STU_ID", studentID);
                                Intent intent = new Intent(StudentAttendanceScreen.this, PersonalAttendance.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        }
                    }
                });
            }
        });
    }

    private void populateSpinner() {

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        //Prepares spinner
        final List<String> modulesList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, modulesList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);

        //Searches for modules which has the corresponding student ID and adds them to spinner
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

    private void getRecord() {

        //Determines database path for user's document
        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        //Gets student ID from the user's document by using their UID
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()) {

                        String studentID = documentSnapshot.getString("student_id");

                        Log.d("STU_ATT", "stuId: " + studentID);

                        assert studentID != null;
                        final CollectionReference recordRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("AttendanceRecord")
                                .document(studentID)
                                .collection("Records");

                        //Finds total number of lectures
                        Query totalQuery = recordRef.orderBy("date", Query.Direction.DESCENDING);
                        totalQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {

                                    int totalCounter = 0;

                                    for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                        totalCounter++;
                                    }

                                    Log.d("STU_ATT", "Total: " + totalCounter);

                                    //Gets amount of lectures the student attended
                                    Query attendedQuery = recordRef.whereEqualTo("attended", true);
                                    final int finalTotalCounter = totalCounter;
                                    attendedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()) {

                                                int attendedCounter = 0;

                                                for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                    attendedCounter++;

                                                }

                                                String totalAttendance = "Total Attendance: " + attendedCounter + "/" + finalTotalCounter;
                                                totalAttTV.setText(totalAttendance);

                                                String lecAttended = "Total Lectures Attended: " + attendedCounter;
                                                lecAttTV.setText(lecAttended);

                                                Log.d("STU_ATT", "Attended: " + attendedCounter + " " + "Total: " + finalTotalCounter);

                                                //Gets total percentage of attendance formatted to two decimal places
                                                float totalPercentage = (float) attendedCounter / finalTotalCounter * 100;
                                                String formattedNum = String.format(Locale.getDefault(), "%.2f", totalPercentage);

                                                /*Set progress of circular ProgressBar and shows attendance percentage in a TextView
                                                Math.round is used to convert percentage to an int because the progress bar can
                                                only use ints to set progress
                                                 */
                                                int percentage = Math.round(totalPercentage);
                                                String showPercent = formattedNum + "%";

                                                //Converts percentage to a float for the isNaN check
                                                float floatPercent = Float.parseFloat(formattedNum);

                                                //Checks if there is any value shown for the percentage TextView
                                                if(Double.isNaN(floatPercent))
                                                {
                                                    String attStr = "No Attendance";
                                                    percentageTV.setText(attStr);
                                                }
                                                else {
                                                    percentageTV.setText(showPercent);
                                                }
                                                attendProgress.setProgress(percentage, true);

                                                //Finds number of missed lectures
                                                Query missedQuery = recordRef.whereEqualTo("attended", false);
                                                missedQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if(task.isSuccessful()) {

                                                            int missedCounter = 0;

                                                            for(QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                                missedCounter++;
                                                            }

                                                            String missedLec = "Total Lectures Missed: " + missedCounter;
                                                            lecMissedTV.setText(missedLec);
                                                        }
                                                        else {
                                                            Log.d("STU_ATT", "Error: " + task.getException());
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Log.d("STU_ATT", "Error: " + task.getException());
                                            }
                                        }
                                    });
                                }
                                else {
                                    Log.d("STU_ATT", "Error: " + task.getException());
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
