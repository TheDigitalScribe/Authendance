package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "document";

    private EditText emailField;
    private EditText passwordField;
    private Button signInBtn;
    private TextView forgotPasswordText;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener fAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signInBtn = findViewById(R.id.signInBtn);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        db = FirebaseFirestore.getInstance();

        fAuth = FirebaseAuth.getInstance();

        fAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fUser = firebaseAuth.getCurrentUser();
                //User is logged in
                if(fUser != null) {
                    Log.d("login", "user found");
                    //Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                }
                //User is logged out
                else {
                    Log.d("login", "user not found");
                    //Toast.makeText(MainActivity.this, "Login unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        };

        forgotPasswordText.setVisibility(View.INVISIBLE);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                //If email and password fields are properly filled in
                if(validateFields()) {
                    fAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Log.d("login", "login successful");

                                        //User's UID is retrieved to find their record in the database
                                        String uid = fAuth.getCurrentUser().getUid();

                                        //Reference to current user's record in the database
                                        DocumentReference userRef = db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("User")
                                                .document(uid);

                                        //This code retrieves the value of the "user_type" field in the record to determine if the user is a student, teacher or admin
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if(document != null) {
                                                        String userType = document.getString("user_type");

                                                        switch(userType) {
                                                            case "Admin":
                                                                Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
                                                                adminIntent.putExtra("FULL_NAME", document.getString("name"));
                                                                startActivity(adminIntent);
                                                                break;
                                                            case "Teacher":
                                                                Intent teacherIntent = new Intent(MainActivity.this, TeacherActivity.class);
                                                                teacherIntent.putExtra("FULL_NAME", document.getString("name"));
                                                                startActivity(teacherIntent);
                                                                break;
                                                            case "Student":
                                                                Intent studentIntent = new Intent(MainActivity.this, StudentActivity.class);
                                                                studentIntent.putExtra("FULL_NAME", document.getString("name"));
                                                                startActivity(studentIntent);
                                                                break;
                                                            default:
                                                                Log.d("login", "User type could not be determined");
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(MainActivity.this, "Record doesn't exist", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Log.d("login", "login unsuccessful");
                                    }
                                }
                            });
                }
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, PasswordReset.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(fAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fAuth.removeAuthStateListener(fAuthListener);
    }

    //Ensures email and password fields are filled out correctly before login
    private boolean validateFields() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Please enter your email address");
            emailField.requestFocus();
            return false;
        }
        else if (password.isEmpty()) {
            passwordField.setError("Please enter your password");
            passwordField.requestFocus();
            return false;
        }
        else {
            emailField.setError(null);
            passwordField.setError(null);
            return true;
        }
    }
}
