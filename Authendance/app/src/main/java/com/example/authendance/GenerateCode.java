package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = "GEN_CODE";

    private Button genCodeBtn;
    private Spinner spinner;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        genCodeBtn = findViewById(R.id.scanCodeBtn);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Modules, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        uid = fAuth.getCurrentUser().getUid();
        //Log.d(TAG, uid);

        if(user != null) {
            Log.d(TAG, "User found");
        }
        else {
            Log.d(TAG, "User not found");
        }

        //Log.d(TAG, genRandomString());

        populateSpinner();

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });
    }

    private void populateSpinner() {

        //Searches for the student's record based on their UID
        CollectionReference studentRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        final List<String> modules = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, modules);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        studentRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        String module = document.getString("name");
                        modules.add(module);
                        //Log.d(TAG, module);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }


    private void generateCode() {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        //Log.d(TAG, "UID: " + uid);

        //Searches for the teacher's record based on the UID
        DocumentReference teacherRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        //Gets teacher_id from the current user's record
        teacherRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if(document != null) {
                        String teacherID = document.getString("teacher_id");
                        //Log.d(TAG, "Teacher ID: " + teacherID);

                        final String spinnerValue = spinner.getSelectedItem().toString();
                        //Log.d(TAG, "Spinner value: " + spinnerValue);

                        CollectionReference sectionRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Section");

                        //Looks for a document where the module name is the same as the spinner value
                        final Query query = sectionRef.whereEqualTo("module", spinnerValue);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(final QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                                        final String docID = documentSnapshot.getId();
                                        //Log.d(TAG, "docID: " + docID);

                                        //The ID of the corresponding document is retrieved
                                        final DocumentReference documentReference = db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("Section")
                                                .document(docID);

                                        //Random QR code generated in the qr_code field of selected module
                                        documentReference.update("qr_code", genRandomString())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Code generated successfully");

                                                        //Retrieves the value of the qr_code field to pass into the
                                                        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                if(task.isSuccessful()) {

                                                                    DocumentSnapshot snapshot = task.getResult();
                                                                    if(snapshot != null) {
                                                                        String qrCode = snapshot.getString("qr_code");
                                                                        Log.d(TAG, "QR code: " + qrCode);

                                                                        Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                                        intent.putExtra("QR_CODE", qrCode);
                                                                        intent.putExtra("MOD_ID", docID);
                                                                        startActivity(intent);
                                                                    }
                                                                }
                                                            }
                                                        });

                                                        CollectionReference studentCol = db.collection("School")
                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                .collection("Section")
                                                                .document(docID)
                                                                .collection("Students");

                                                        studentCol.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                if(task.isSuccessful()) {
                                                                    for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                                                                        if(snapshot != null) {

                                                                            WriteBatch batch = db.batch();
                                                                            Map<String, Object> students = new HashMap<>();
                                                                            students.put(snapshot.getId(), false);
                                                                            //Log.d(TAG, "Ids: " + stuID);

                                                                            Calendar calendar = Calendar.getInstance();
                                                                            String currentDate = DateFormat.getDateInstance().format(calendar.getTime());

                                                                            db.collection("School")
                                                                                .document("0DKXnQhueh18DH7TSjsb")
                                                                                .collection("Attendance")
                                                                                .document(docID)
                                                                                .collection("Date")
                                                                                .document(currentDate);



                                                                        }
                                                                    }
                                                                }

                                                            }
                                                        });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "Code failed to generate");
                                                    }
                                                });
                                    }
                                }
                                else {
                                    Log.d(TAG, "Query failed");
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(GenerateCode.this, "Document not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private String genRandomString() {
        char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < 25; i++) {
            char c = characters[random.nextInt(characters.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
