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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleSelect extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = "MOD_PICK";

    private Boolean codeCheck;
    private String uid;
    private FirebaseFirestore db;

    private String studentID;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_picker);

        Button scanCodeBtn = findViewById(R.id.scanCodeBtn);
        spinner = findViewById(R.id.spinner);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        codeCheck = false;

        if (user != null) {
            Log.d(TAG, "User found");
        } else {
            Log.d(TAG, "User not found");
        }

        //Retrieves student ID from StudentActivity.class
        Intent intent = getIntent();
        studentID = intent.getStringExtra("STUDENT_ID");

        populateSpinner();

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleCheck();
            }
        });
    }

    private void populateSpinner() {

        //Determines database path for the user's modules
        final CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid)
                .collection("Modules");

        //Setting up the spinner which allows users to pick modules
        final List<String> modulesList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, modulesList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);

        //Searches for modules the current student user is enrolled in and adds them to the spinner
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        modulesList.add(queryDocumentSnapshot.getId());
                        Log.d(TAG, "Modules: " + modulesList);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //To check if a QR code was generated for the selected module in the spinner
    private void moduleCheck() {

        //Spinner value determines which module they want to record their attendance for
        final String spinnerValue = spinner.getSelectedItem().toString();

        //Determines document path for the chosen module
        DocumentReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Modules")
                .document(spinnerValue);

        //Finds correct document to retrieve the QR code and module ID
        moduleRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {

                        //Retrieves QR code and document ID for the module
                        String qrCode = documentSnapshot.getString("qr_code");
                        String moduleID = documentSnapshot.getId();

                        //If qr_code is not null. Meaning the QR code has not been generated
                        if (qrCode != null) {
                            codeCheck = true;
                        }

                        //If a code was generated for the module, pass in the module name and document ID to the CodeScanner class
                        if (codeCheck.equals(true)) {
                            Bundle data = new Bundle();
                            data.putString("MOD_ID", moduleID);
                            data.putString("QR_CODE", qrCode);
                            data.putString("STU_ID", studentID);
                            Intent intent = new Intent(ModuleSelect.this, CodeScanner.class);
                            intent.putExtras(data);
                            startActivity(intent);
                        } else if (codeCheck.equals(false)) {
                            Toast.makeText(ModuleSelect.this, "Code not generated for this module", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(ModuleSelect.this, "No document exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ModuleSelect.this, "Error. " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Methods for the spinner. Unused but required
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
