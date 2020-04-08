package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button signInBtn;
    private TextView registerPrompt;
    private TextView forgotPasswordText;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signInBtn = findViewById(R.id.signInBtn);
        registerPrompt = findViewById(R.id.registerPrompt);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        mAuth = FirebaseAuth.getInstance();

        forgotPasswordText.setVisibility(View.INVISIBLE);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mAuth.getCurrentUser();

                if(mFirebaseUser != null) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Please login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if(validateEmail() && validatePassword()) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                forgotPasswordText.setVisibility(View.VISIBLE);
                                Toast.makeText(MainActivity.this, "No user found with these details", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Intent intent = new Intent(MainActivity.this, HomeScreen.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });


        registerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterScreen.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private boolean validateEmail() {
        String email = emailField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Please enter your email address");
            emailField.requestFocus();
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        }
        else {
            emailField.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String password = passwordField.getText().toString().trim();

        if(password.isEmpty()) {
            passwordField.setError("Please enter your password");
            passwordField.requestFocus();
            return false;
        }
        else {
            passwordField.setError(null);
            return true;
        }
    }
}
