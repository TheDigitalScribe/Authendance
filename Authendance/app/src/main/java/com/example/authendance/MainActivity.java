package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "document";

    private EditText emailField;
    private EditText passwordField;
    private Button signInBtn;
    private TextView registerPrompt;
    private TextView forgotPasswordText;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseAuth.AuthStateListener fAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signInBtn = findViewById(R.id.signInBtn);
        registerPrompt = findViewById(R.id.registerPrompt);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);

        fAuth = FirebaseAuth.getInstance();

        forgotPasswordText.setVisibility(View.INVISIBLE);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailField.getText().toString();
                final String password = passwordField.getText().toString();
                final FirebaseUser user = fAuth.getCurrentUser();
                db = FirebaseFirestore.getInstance();

                if (validateFields()) {
                    final CollectionReference usersRef = db.collection("School").document("0DKXnQhueh18DH7TSjsb").collection("User");

                    /*User's email is retrieved from Firestore. If user logs in successfully,
                    their UID is retrieved which finds their record to determine their user type
                    and direct them to the appropriate activity.
                     */
                    Query query = usersRef.whereEqualTo("email", email);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                                usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            assert document != null;
                                                            if (document.exists()) {
                                                                String userType = document.getString("user_type");
                                                                String name = document.getString("name");
                                                                Toast.makeText(MainActivity.this, "Welcome, " + name, Toast.LENGTH_SHORT).show();

                                                                assert userType != null;
                                                                switch(userType) {
                                                                    case "Admin":
                                                                        Intent adminIntent = new Intent(MainActivity.this, AdminActivity.class);
                                                                        adminIntent.putExtra("FULL_NAME", name);
                                                                        startActivity(adminIntent);
                                                                        break;
                                                                    case "Teacher":
                                                                        Intent teacherIntent = new Intent(MainActivity.this, TeacherActivity.class);
                                                                        teacherIntent.putExtra("FULL_NAME", name);
                                                                        startActivity(teacherIntent);
                                                                        break;
                                                                    case "Student":
                                                                        Intent studentIntent = new Intent(MainActivity.this, StudentActivity.class);
                                                                        studentIntent.putExtra("FULL_NAME", name);
                                                                        startActivity(studentIntent);
                                                                        break;
                                                                    default:
                                                                        Toast.makeText(MainActivity.this, "User type cannot be determined", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                            else if(!task.isSuccessful()) {
                                                Toast.makeText(MainActivity.this, "Error. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });

        registerPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent = new Intent(MainActivity.this, RegisterScreen.class);
                startActivity(intent);*/

                Log.d("uid", "UID: " + fAuth.getUid());
            }
        });

        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }

    private boolean validateFields() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if (email.isEmpty()) {
            emailField.setError("Please enter your email address");
            emailField.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Please enter a valid email address");
            emailField.requestFocus();
            return false;
        } else if (password.isEmpty()) {
            passwordField.setError("Please enter your password");
            passwordField.requestFocus();
            return false;
        } else {
            emailField.setError(null);
            passwordField.setError(null);
            return true;
        }
    }
}
