package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class RegisterScreen extends AppCompatActivity {

    //regex validation for password field
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    "(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

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
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if(validateEmail() && validatePassword()) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegisterScreen.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()) {
                                Toast.makeText(RegisterScreen.this, "Signup unsuccessful", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegisterScreen.this, "Signup successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterScreen.this, StudentActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean validateEmail() {
        String email = emailField.getText().toString().trim();

        if(email.isEmpty()) {
            emailField.setError("Please enter email address");
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
            passwordField.setError("Password can't be empty");
            passwordField.requestFocus();
            return false;
        }
        else if(!PASSWORD_PATTERN.matcher(password).matches()) {
            passwordField.setError("Password must contain at least 1 lowercase character, 1 uppercase character, 1 special character and must be 6 or more characters");
            passwordField.requestFocus();
            return false;
        }
        else {
            passwordField.setError(null);
            return true;
        }
    }
}
