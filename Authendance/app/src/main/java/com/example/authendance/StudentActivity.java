package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudentActivity extends AppCompatActivity {

    private TextView nameDisplay;
    private CardView scanCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        nameDisplay = findViewById(R.id.nameDisplay);
        scanCard = findViewById(R.id.scanCard);

        Intent intent = getIntent();
        String name = intent.getStringExtra("FULL_NAME");

        nameDisplay.setText(name);

        scanCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentActivity.this, CodeScanner.class);
                startActivity(intent);
            }
        });
    }
}
