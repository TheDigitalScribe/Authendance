package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Objects;

public class StudentActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        TextView nameDisplay = findViewById(R.id.nameDisplay);
        TextView idDisplay = findViewById(R.id.idDisplay);
        CardView scanCard = findViewById(R.id.scanCard);
        CardView moduleCard = findViewById(R.id.moduleCard);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getNameID(nameDisplay, idDisplay);

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, ModulePicker.class);
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

    //Method to get current user's name to display on the dashboard
    private void getNameID(final TextView nameDisplay, final TextView idDisplay) {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference studentRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        studentRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if(task.isSuccessful()) {
                    if(documentSnapshot != null) {
                        String studentName = documentSnapshot.getString("name");
                        String studentID = documentSnapshot.getString("student_id");
                        Log.d("studentName", "Student name: " + studentName);

                        nameDisplay.setText(studentName);
                        idDisplay.setText(studentID);
                    }
                    else {
                        Toast.makeText(StudentActivity.this, "Document not found", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(StudentActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
