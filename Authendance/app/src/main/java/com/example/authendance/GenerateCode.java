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

import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final long START_TIME_IN_MILLIS = 600000;
    private final static String TAG = "GEN_CODE";

    private Button genCodeBtn;
    private Spinner spinner;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;


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

        if(user != null) {
            Log.d(TAG, "User found");
        }
        else {
            Log.d(TAG, "User not found");
        }

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
            }
        });
    }

    private void generateCode() {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        Log.d(TAG, "UID: " + uid);

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
                        Log.d(TAG, "Teacher ID: " + teacherID);

                        final String spinnerValue = spinner.getSelectedItem().toString();
                        Log.d(TAG, "Spinner value: " + spinnerValue);

                        CollectionReference sectionRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Section");

                        //Looks for a document where the module name is the same as the spinner value
                        Query query = sectionRef.whereEqualTo("module", spinnerValue);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                                        final String docID = documentSnapshot.getId();
                                        Log.d(TAG, "docID: " + docID);

                                        //The ID of the corresponding document is retrieved and used to update the code_generated field to true
                                        DocumentReference documentReference = db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("Section")
                                                .document(docID);

                                        documentReference.update("code_generated", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Code generated successfully");

                                                        Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                        intent.putExtra("QR_CODE", docID);
                                                        startActivity(intent);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
