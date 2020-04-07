package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterScreen extends AppCompatActivity {

    private EditText emailField;
    private EditText passwordField;
    private Button signUpBtn;
    private TextView returnSignInPrompt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signUpBtn = findViewById(R.id.signUpBtn);
        returnSignInPrompt = findViewById(R.id.returnSignInPrompt);
        mAuth = FirebaseAuth.getInstance();

        returnSignInPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                if(email.isEmpty()) {
                    emailField.setError("Please enter email address!");
                    emailField.requestFocus();
                }
                else if(password.isEmpty()) {
                    passwordField.setError("Please enter password!");
                    passwordField.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty()) {
                    Toast.makeText(RegisterScreen.this, "Both fields are blank!", Toast.LENGTH_SHORT).show();
                }
                else if(!(email.isEmpty() && password.isEmpty())) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(RegisterScreen.this, "Signup unsuccessful!", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent(RegisterScreen.this, HomeScreen.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterScreen.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
