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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.rpc.Code;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModulePicker extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final static String TAG = "MOD_PICK";

    private Button scanCodeBtn;
    private Spinner stuSpinner;
    private Boolean codeCheck;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_picker);

        scanCodeBtn = findViewById(R.id.scanCodeBtn);

        stuSpinner = findViewById(R.id.stuSpinner);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uid = fAuth.getCurrentUser().getUid();

        codeCheck = false;

        if(user != null) {
            Log.d(TAG, "User found");
        }
        else {
            Log.d(TAG, "User not found");
        }

        populateSpinner();

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moduleCheck();
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
        stuSpinner.setAdapter(adapter);

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

    private void moduleCheck() {

        //Log.d(TAG, "UID: " + uid);

        //Searches for the student's record based on their UID
        DocumentReference studentRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        studentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();

                    //Retrieves student's ID from their record if document exists
                    if(document != null) {
                        final String studentID = document.getString("student_id");
                        //Log.d(TAG, "Student ID: " + studentID);

                        //Spinner value determines which module they want to record their attendance for
                        final String spinnerValue = stuSpinner.getSelectedItem().toString();
                        //Log.d(TAG, "Spinner value: " + spinnerValue);

                        CollectionReference sectionRef = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("Section");

                        //Looks for a document where the module name is the same as the spinner value
                        Query query = sectionRef.whereEqualTo("module", spinnerValue);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult())) {

                                        codeCheck = documentSnapshot.getBoolean("code_generated");
                                        final String docID = documentSnapshot.getId();
                                        final String moduleName = documentSnapshot.getString("module");
                                        //Log.d(TAG, "docID: " + docID);
                                        //Log.d(TAG, "code_generated: " + codeCheck);

                                        if(codeCheck.equals(true)) {
                                            Bundle data = new Bundle();
                                            data.putString("MOD_NAME", moduleName);
                                            data.putString("QR_CODE", docID);
                                            Intent intent = new Intent(ModulePicker.this, CodeScanner.class);
                                            intent.putExtras(data);
                                            startActivity(intent);
                                        }

                                        else if(codeCheck.equals(false)) {
                                            Toast.makeText(ModulePicker.this, "Code not generated for this module" ,Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                                else {
                                    Toast.makeText(ModulePicker.this, "Query to retrieve data failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else {
                        Toast.makeText(ModulePicker.this, "Student record does not exist", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(ModulePicker.this, "Student could not be found", Toast.LENGTH_SHORT).show();;
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
