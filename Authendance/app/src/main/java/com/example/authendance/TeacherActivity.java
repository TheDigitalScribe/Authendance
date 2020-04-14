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

import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        TextView nameDisplay = findViewById(R.id.nameDisplay);
        CardView genCard = findViewById(R.id.genCard);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();

        getName(nameDisplay);

        genCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, GenerateCode.class);
                startActivity(intent);
            }
        });
    }

    private void getName(final TextView nameDisplay) {

        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        //Looks for teacher's record in the database by using their UID
        DocumentReference teacherRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        teacherRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();

                if (task.isSuccessful()) {
                    if (documentSnapshot != null) {
                        String teacherName = documentSnapshot.getString("name");

                        nameDisplay.setText(teacherName);
                    } else {
                        Toast.makeText(TeacherActivity.this, "Document not found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TeacherActivity.this, "Task failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
