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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class TeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        TextView nameDisplay = findViewById(R.id.nameDisplay);
        TextView idDisplay = findViewById(R.id.idDisplay);
        CardView genCard = findViewById(R.id.genCard);
        CardView moduleCard = findViewById(R.id.moduleCard);
        CardView attendanceCard = findViewById(R.id.attendanceCard);
        CardView settingsCard = findViewById(R.id.settingsCard);

        //Gets teacher name and ID from MainActivity
        Intent intent = getIntent();
        final String teacherName = intent.getStringExtra("TEACHER_NAME");
        final String teacherID = intent.getStringExtra("TEACHER_ID");

        nameDisplay.setText(teacherName);
        idDisplay.setText(teacherID);

        genCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
