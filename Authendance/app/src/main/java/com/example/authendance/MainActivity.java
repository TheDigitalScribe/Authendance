package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        Button scanCodeBtn = findViewById(R.id.scanCodeBtn);
        Button genCodeBtn = findViewById(R.id.genCodeBtn);

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CodeScanner.class);
                startActivity(intent);
            }
        });

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GenerateCode.class);
                startActivity(intent);
            }
        });
    }
}
