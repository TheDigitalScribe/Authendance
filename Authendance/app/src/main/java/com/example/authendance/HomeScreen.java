package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeScreen extends AppCompatActivity {

    TextView welcomeText;
    FirebaseUser user;
    Button genCodeBtn;
    Button scanCodeBtn;
    Button signOutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        welcomeText = findViewById(R.id.welcomeText);
        genCodeBtn = findViewById(R.id.genCodeBtn);
        scanCodeBtn = findViewById(R.id.scanCodeBtn);
        signOutBtn = findViewById(R.id.signOutBtn);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String name = "Hello, " + email;

        welcomeText.setText(name);

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, GenerateCode.class);
                startActivity(intent);
            }
        });

        scanCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this, CodeScanner.class);
                startActivity(intent);
            }
        });

        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeScreen.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(HomeScreen.this, "Signed out.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
