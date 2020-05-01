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
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {

    TextView nameDisplay;
    TextView idDisplay;
    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);
        CardView genCard = findViewById(R.id.genCard);
        CardView moduleCard = findViewById(R.id.moduleCard);
        CardView attendanceCard = findViewById(R.id.attendanceCard);
        CardView settingsCard = findViewById(R.id.settingsCard);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getNameID();

        genCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String teacherName = nameDisplay.getText().toString();
                String teacherID = idDisplay.getText().toString();

                Intent intent = new Intent(TeacherActivity.this, GenerateCode.class);
                intent.putExtra("TEACHER_NAME", teacherName);
                intent.putExtra("TEACHER_ID", teacherID);
                startActivity(intent);
            }
        });

        moduleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, TeacherModules.class);
                startActivity(intent);
            }
        });

        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, AttendanceSelect.class);
                startActivity(intent);
            }
        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Asks the user if they want to log out when they press the back button
    @Override
    public void onBackPressed() {

        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(TeacherActivity.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(TeacherActivity.this, MainActivity.class);
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

    //Retrieves current user's name and ID from database and displays it
    private void getNameID() {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();

                    assert documentSnapshot != null;
                    if (documentSnapshot.exists()) {

                        String teacherName = documentSnapshot.getString("name");
                        String teacherID = documentSnapshot.getString("teacher_id");

                        nameDisplay.setText(teacherName);
                        idDisplay.setText(teacherID);
                    } else {
                        Toast.makeText(TeacherActivity.this, "Document doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TeacherActivity.this, "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
