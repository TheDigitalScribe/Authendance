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
import android.widget.NumberPicker;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = "GEN_CODE";

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private Spinner teacherSpinner;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        Button genCodeBtn = findViewById(R.id.scanCodeBtn);
        teacherSpinner = findViewById(R.id.spinner);
        //NumberPicker numberPicker = findViewById(R.id.numberPicker);

        //numberPicker.setMinValue(1);
        //numberPicker.setMaxValue(5);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = fAuth.getCurrentUser().getUid();

        populateSpinner();

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateCode();
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        teacherSpinner.setAdapter(adapter);

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

    private void generateCode() {

        final String spinnerValue = teacherSpinner.getSelectedItem().toString();

        final DocumentReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Modules")
                .document(spinnerValue);

        moduleRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        final String moduleID = documentSnapshot.getId();

                        final DocumentReference documentReference = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Modules")
                                .document(moduleID);

                        documentReference.update("qr_code", genRandomString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Code generated successfully");

                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot snapshot = task.getResult();

                                            if (snapshot.exists()) {
                                                String qrCode = snapshot.getString("qr_code");

                                                String currentDate = java.text.DateFormat.getDateInstance().format(new Date());

                                                Map<String, Object> module = new HashMap<>();
                                                module.put("module", moduleID);

                                                Student student = new Student(null);

                                                //Adds module to Attendance collection
                                                db.collection("School")
                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                        .collection("Attendance")
                                                        .document(moduleID)
                                                        .set(module);

                                                //Adds empty array for students who have attended on the current date
                                                db.collection("School")
                                                        .document("0DKXnQhueh18DH7TSjsb")
                                                        .collection("Attendance")
                                                        .document(moduleID)
                                                        .collection("Date")
                                                        .document(currentDate)
                                                        .set(student);

                                                Intent intent = new Intent(GenerateCode.this, CodeScreen.class);
                                                intent.putExtra("QR_CODE", qrCode);
                                                intent.putExtra("MOD_ID", moduleID);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
