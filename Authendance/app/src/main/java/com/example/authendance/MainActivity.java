package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "login";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";

    private EditText emailField;
    private EditText passwordField;
    private String emailContent;
    private String passwordContent;
    private ProgressBar progressBar;
    private TextView forgotPasswordText;
    Button signInBtn;

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
        progressBar = findViewById(R.id.circleProgressBar);

        progressBar.setVisibility(View.GONE);

        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        fAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser fUser = firebaseAuth.getCurrentUser();
                //User is logged in
                if (fUser != null) {
                    Log.d(TAG, "user found");
                }
                //User is logged out
                else {
                    Log.d(TAG, "user not found");
                }

            }
        };

        forgotPasswordText.setVisibility(View.INVISIBLE);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();

                progressBar.setVisibility(View.VISIBLE);

                //If email and password fields are properly filled in
                if(validateEmail() && validatePassword()) {
                    fAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        Log.d(TAG, "Login successful");
                                        saveData();

                                        //User's UID is retrieved to find their record in the database
                                        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

                                        //Searches for current user's record in the database based on UID
                                        DocumentReference userRef = db.collection("School")
                                                .document("0DKXnQhueh18DH7TSjsb")
                                                .collection("User")
                                                .document(uid);

                                        //This code retrieves the user type, name and ID of the user
                                        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if(document != null) {
                                                        String userType = document.getString("user_type"); //This determines if the user is a teacher or student
                                                        String userName = document.getString("name");
                                                        String studentID = document.getString("student_id");
                                                        String teacherID = document.getString("teacher_id");

                                                        assert userType != null;
                                                        switch(userType) {
                                                            case "Teacher":
                                                                Intent teacherIntent = new Intent(MainActivity.this, TeacherActivity.class);
                                                                teacherIntent.putExtra("TEACHER_NAME", userName);
                                                                teacherIntent.putExtra("TEACHER_ID", teacherID);
                                                                Toast.makeText(MainActivity.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                                                startActivity(teacherIntent);
                                                                break;
                                                            case "Student":
                                                                Intent studentIntent = new Intent(MainActivity.this, StudentActivity.class);
                                                                studentIntent.putExtra("STUDENT_NAME", userName);
                                                                studentIntent.putExtra("STUDENT_ID", studentID);
                                                                Toast.makeText(MainActivity.this, "Welcome, " + userName, Toast.LENGTH_SHORT).show();
                                                                startActivity(studentIntent);
                                                                break;
                                                            default:
                                                                Toast.makeText(MainActivity.this, "User type could not be determined.", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else {
                                                        Log.d(TAG, "Document not found");
                                                    }
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                        forgotPasswordText.setVisibility(View.VISIBLE);
                                        Log.d(TAG, Objects.requireNonNull(task.getException().getMessage()));
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

        loadData();
        updateFields();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        progressBar.setVisibility(View.GONE);
        forgotPasswordText.setVisibility(View.GONE);
    }

    /*This ensures when the user logs out and is brought back to the login screen,
     they can't bypass the login screen by pressing the back button*/
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    //Ensures email filled is filled out correctly before login
    private boolean validateEmail() {
        String email = emailField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Please enter your email address");
            emailField.requestFocus();
            return false;
        }

        //Ensures email address follows a certain pattern. Select EMAIL_ADDRESS and click "CTRL B" for more info
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
            passwordField.setError("Please enter password");
            passwordField.requestFocus();
            return false;
        }
        else {
            passwordField.setError(null);
            return true;
        }
    }

    //Saves the valid email and password that were last entered in the text fields
    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(EMAIL, emailField.getText().toString());
        editor.putString(PASSWORD, passwordField.getText().toString());

        editor.apply();

        Log.d(TAG, "Email: " + emailField.getText().toString() + " and password " + passwordField.getText().toString() + " saved");
    }

    //Loads the last entered email and password
    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        emailContent = sharedPreferences.getString(EMAIL, "");
        passwordContent = sharedPreferences.getString(PASSWORD, "");
    }

    //Sets the text of these text fields to whatever was saved
    public void updateFields() {
        emailField.setText(emailContent);
        passwordField.setText(passwordContent);
    }
}
