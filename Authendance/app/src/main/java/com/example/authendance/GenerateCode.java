package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final long START_TIME_IN_MILLIS = 600000;
    private final static String TAG = "GEN_CODE";

    private TextView codeField;
    private Spinner spinner;
    private ImageView qrCode;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        codeField = findViewById(R.id.codeField);
        Button genCodeBtn = findViewById(R.id.scanCodeBtn);

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

                addQRCOde();

            }
        });
    }

    //Method for generating random string for QR code generation
    private String genRandString() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 26; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private void addQRCOde() {

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

                        final CollectionReference sectionRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Section");

                        //Finds the correct document in the Section collection according to the spinner value
                        Query query = sectionRef.whereEqualTo("module", spinnerValue);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        Log.d(TAG, documentSnapshot.getId());

                                        final String docID = documentSnapshot.getId();
                                        final String code = genRandString();

                                        Map<String, Object> data = new HashMap<>();
                                        data.put("qrcode", code);

                                        DocumentReference documentReference = db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("Section")
                                                .document(docID);

                                        documentReference.update("qrcode", code)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "QR code added");

                                                        //Passes document ID and qr code to CodeScreen activity
                                                        Bundle data = new Bundle();
                                                        data.putString("DOC_ID", docID);
                                                        data.putString("QR_CODE", code);

                                                        Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                        intent.putExtras(data);
                                                        startActivity(intent);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "QR not added");
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
