package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class StudentActivity extends AppCompatActivity {

    private long backPressed;

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
        CardView settingsCard = findViewById(R.id.settingsCard);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getNameID();

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ModuleSelect.class);
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

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        //Checks if back button was clicked twice within 2 seconds
        if(backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentActivity.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(StudentActivity.this, MainActivity.class);
                    startActivity(intent);
                    fAuth.signOut();
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
        }
        backPressed = System.currentTimeMillis();
    }

    private void getNameID() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

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
