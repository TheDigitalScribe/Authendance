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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class PasswordReset extends AppCompatActivity {

    private EditText resetEmailField;
    private Button submitBtn;
    private TextView returnLoginText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        resetEmailField = findViewById(R.id.resetEmailField);
        submitBtn = findViewById(R.id.submitBtn);
        returnLoginText = findViewById(R.id.returnLoginText);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmail()) {

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = resetEmailField.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordReset.this, "Reset Password email sent. Please check email", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(PasswordReset.this, "Error. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        returnLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PasswordReset.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateEmail() {
        String resetEmail = resetEmailField.getText().toString().trim();

        if (resetEmail.isEmpty()) {
            resetEmailField.setError("Please enter your email address");
            resetEmailField.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(resetEmail).matches()) {
            resetEmailField.setError("Please enter a valid email address");
            resetEmailField.requestFocus();
            return false;
        } else {
            resetEmailField.setError(null);
            return true;
        }
    }
}
