package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = "GEN_CODE";

    private FirebaseFirestore db;
    private String uid;
    private Spinner teacherSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        Button genCodeBtn = findViewById(R.id.genCodeBtn);
        teacherSpinner = findViewById(R.id.spinner);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        populateSpinner();

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });
    }


    private void populateSpinner() {

        //Searches for the teacher's modules in their database record
        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        //Prepares spinner and dropdown list
        final List<String> modulesList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, modulesList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        teacherSpinner.setAdapter(adapter);

        //Retrieves the IDs of the modules and adds them to the list for the spinner
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String moduleName = queryDocumentSnapshot.getId();
                        modulesList.add(moduleName);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateCode() {

        final String spinnerValue = teacherSpinner.getSelectedItem().toString();

        //Searches for the module the user selected in the spinner
        final DocumentReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Modules")
                .document(spinnerValue);

        moduleRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {

                        //Retrieves the ID of the corresponding module document
                        final String moduleID = documentSnapshot.getId();

                        final DocumentReference documentReference = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Modules")
                                .document(moduleID);

                        //Updates the qr_code field in the right module document with a randomised char string
                        documentReference.update("qr_code", genRandomString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Code generated successfully");

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot snapshot = task.getResult();

                                            assert snapshot != null;
                                            if (snapshot.exists()) {
                                                final String qrCode = snapshot.getString("qr_code");

                                                //Gets current date
                                                final String currentDate = new SimpleDateFormat("dd MM YYYY", Locale.getDefault()).format(new Date());

                                                //Adds module field to the generated module document in the Attendance collection
                                                final Map<String, Object> module = new HashMap<>();
                                                module.put("module", moduleID);

                                                //Adds date and attendance field to the generated date document in the Date sub-collection
                                                final Map<String, Object> date = new HashMap<>();
                                                date.put("date", currentDate);
                                                date.put("attendance", 0);

                                                //Adds module to Attendance collection
                                                db.collection("School")
                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                        .collection("Attendance")
                                                        .document(moduleID)
                                                        .set(module);

                                                DocumentReference dateCheck = db.collection("School")
                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                        .collection("Attendance")
                                                        .document(moduleID)
                                                        .collection("Date")
                                                        .document(currentDate);

                                                //Checks if code was already generated for the selected module on the current date
                                                dateCheck.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {

                                                            DocumentSnapshot docSnap = task.getResult();
                                                            assert docSnap != null;
                                                            if (docSnap.exists()) {

                                                                AlertDialog.Builder builder = new AlertDialog.Builder(GenerateCode.this);
                                                                builder.setTitle("Overwrite Attendance?");
                                                                builder.setMessage("Generating a QR code will overwrite previous attendance. Continue?");
                                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {

                                                                        //Adds current date record to database
                                                                        db.collection("School")
                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                .collection("Attendance")
                                                                                .document(moduleID)
                                                                                .collection("Date")
                                                                                .document(currentDate)
                                                                                .set(date);

                                                                        //Determines database path for all the students enrolled in the selected module
                                                                        CollectionReference studentRef = db.collection("School")
                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                .collection("Modules")
                                                                                .document(moduleID)
                                                                                .collection("Students");

                                                                        //Gets all students enrolled in the module and adds them to the Attendance collection
                                                                        Query query = studentRef.orderBy("student_id", Query.Direction.DESCENDING);
                                                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                                                        String studentID = queryDocumentSnapshot.getId();
                                                                                        Log.d("STU_ID", studentID);

                                                                                        //Attendance sent to false by default. Updated to true when they scan the code
                                                                                        Map<String, Object> attended = new HashMap<>();
                                                                                        attended.put("attended", false);
                                                                                        attended.put("student_id", studentID);

                                                                                        db.collection("School")
                                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                                .collection("Attendance")
                                                                                                .document(moduleID)
                                                                                                .collection("Date")
                                                                                                .document(currentDate)
                                                                                                .collection("Students")
                                                                                                .document(studentID)
                                                                                                .set(attended);

                                                                                        //Adds student ID document to AttendanceRecord collection
                                                                                        Map<String, Object> stuID = new HashMap<>();
                                                                                        stuID.put("student_id", studentID);

                                                                                        db.collection("School")
                                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                                .collection("AttendanceRecord")
                                                                                                .document(studentID)
                                                                                                .set(stuID);

                                                                                        //Adds date, module and attended checker to student's personal attendance record
                                                                                        Map<String, Object> attend = new HashMap<>();
                                                                                        attend.put("date", currentDate);
                                                                                        attend.put("module", moduleID);
                                                                                        attend.put("attended", false);
                                                                                        attend.put("time", FieldValue.serverTimestamp());

                                                                                        //Removes spaces for the ID of the generated attendance document
                                                                                        String docName = moduleID.replaceAll("\\s+", "") + currentDate.replaceAll("\\s+", "");

                                                                                        db.collection("School")
                                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                                .collection("AttendanceRecord")
                                                                                                .document(studentID)
                                                                                                .collection("Records")
                                                                                                .document(docName)
                                                                                                .set(attend, SetOptions.merge());
                                                                                    }
                                                                                }
                                                                            }
                                                                        });

                                                                        //Passes QR Code and module to CodeScreen class
                                                                        Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                                        intent.putExtra("QR_CODE", qrCode);
                                                                        intent.putExtra("MOD_ID", moduleID);
                                                                        startActivity(intent);

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
                                                            } else {

                                                                //Adds current date record to database
                                                                db.collection("School")
                                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                                        .collection("Attendance")
                                                                        .document(moduleID)
                                                                        .collection("Date")
                                                                        .document(currentDate)
                                                                        .set(date);

                                                                //Determines database path for all the students enrolled in the selected module
                                                                CollectionReference studentRef = db.collection("School")
                                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                                        .collection("Modules")
                                                                        .document(moduleID)
                                                                        .collection("Students");

                                                                //Gets all students enrolled in the module and adds them to the Attendance collection
                                                                Query query = studentRef.orderBy("student_id", Query.Direction.DESCENDING);
                                                                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                        if (task.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                                                                                String studentID = queryDocumentSnapshot.getId();
                                                                                Log.d("STU_ID", studentID);

                                                                                //Attendance sent to false by default. Updated to true when they scan the code
                                                                                Map<String, Object> attended = new HashMap<>();
                                                                                attended.put("attended", false);
                                                                                attended.put("student_id", studentID);

                                                                                db.collection("School")
                                                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                                                        .collection("Attendance")
                                                                                        .document(moduleID)
                                                                                        .collection("Date")
                                                                                        .document(currentDate)
                                                                                        .collection("Students")
                                                                                        .document(studentID)
                                                                                        .set(attended);

                                                                                //Adds student ID document to AttendanceRecord collection
                                                                                Map<String, Object> stuID = new HashMap<>();
                                                                                stuID.put("student_id", studentID);

                                                                                db.collection("School")
                                                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                                                        .collection("AttendanceRecord")
                                                                                        .document(studentID)
                                                                                        .set(stuID);

                                                                                //Adds date, module and attended checker to student's personal attendance record
                                                                                Map<String, Object> attend = new HashMap<>();
                                                                                attend.put("date", currentDate);
                                                                                attend.put("module", moduleID);
                                                                                attend.put("attended", false);
                                                                                attend.put("time", FieldValue.serverTimestamp());

                                                                                //Removes spaces for the ID of the generated attendance document
                                                                                String docName = moduleID.replaceAll("\\s+", "") + currentDate.replaceAll("\\s+", "");

                                                                                db.collection("School")
                                                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                                                        .collection("AttendanceRecord")
                                                                                        .document(studentID)
                                                                                        .collection("Records")
                                                                                        .document(docName)
                                                                                        .set(attend, SetOptions.merge());
                                                                            }
                                                                        }
                                                                    }
                                                                });

                                                                //Passes QR Code and module to CodeScreen class
                                                                Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                                intent.putExtra("QR_CODE", qrCode);
                                                                intent.putExtra("MOD_ID", moduleID);
                                                                startActivity(intent);
                                                            }
                                                        }
                                                        else {
                                                            Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(GenerateCode.this, "Nothing found.", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        else {
                                            Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                    else {
                        Toast.makeText(GenerateCode.this, "Document doesn't exist.", Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(GenerateCode.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Generates random string for the QR code
    private String genRandomString() {
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 25; i++) {
            char c = characters[random.nextInt(characters.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();

    }

    //Methods for the spinner. Unused but required
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}