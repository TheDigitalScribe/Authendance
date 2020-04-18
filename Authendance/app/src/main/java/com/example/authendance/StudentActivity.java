package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentActivity extends AppCompatActivity {

    public static final String STUDENT_PREFS = "studentPrefs";
    public static final String NAME = "studentName";
    public static final String ID = "studentID";

    private String nameContent;
    private String idContent;

    private FirebaseFirestore db;
    private FirebaseAuth fAuth;

    TextView nameDisplay;
    TextView idDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);
        CardView scanCard = findViewById(R.id.scanCard);
        CardView moduleCard = findViewById(R.id.moduleCard);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getNameID();

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ModulePicker.class);
                String studentName = nameDisplay.getText().toString();
                String studentID = idDisplay.getText().toString();

                intent.putExtra("STUDENT_NAME", studentName);
                intent.putExtra("STUDENT_ID", studentID);
                startActivity(intent);
            }
        });

        moduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, StudentModules.class);
                startActivity(intent);
            }
        });
    }

    private void getNameID() {
        String uid = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    if(documentSnapshot.exists()) {
                        String studentName = documentSnapshot.getString("name");
                        String studentID = documentSnapshot.getString("student_id");

                        nameDisplay.setText(studentName);
                        idDisplay.setText(studentID);
                    }
                }
            }
        });
    }




}
