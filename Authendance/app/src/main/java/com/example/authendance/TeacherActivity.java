package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TeacherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        TextView nameDisplay = findViewById(R.id.nameDisplay);
        CardView genCard = findViewById(R.id.genCard);

        Intent intent = getIntent();
        String name = intent.getStringExtra("FULL_NAME");

        nameDisplay.setText(name);

        genCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherActivity.this, GenerateCode.class);
                startActivity(intent);
            }
        });
    }
}
