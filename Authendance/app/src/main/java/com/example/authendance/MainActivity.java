package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


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
                    their UID is retrieved which finds their record to determine their user type.
                     */
                    Query query = usersRef.whereEqualTo("email", email);
                    query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String uid = fAuth.getCurrentUser().getUid();
                                                usersRef.document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                String userType = document.getString("user_type");
                                                                if (userType.equals("admin")) {
                                                                    Intent intent = new Intent(MainActivity.this, TeacherActivity.class);
                                                                    startActivity(intent);
                                                                    Toast.makeText(MainActivity.this, "Welcome, " + document.getString("name"), Toast.LENGTH_SHORT).show();
                                                                } else if (userType.equals("student")) {
                                                                    Intent intent = new Intent(MainActivity.this, StudentActivity.class);
                                                                    startActivity(intent);
                                                                    Toast.makeText(MainActivity.this, "Welcome, " + document.getString("name"), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                            else {
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

                addUser();
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

    private void addUser() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", "Mark");
        user.put("email", "c654321@mytudublin.ie");
        user.put("user_type", "student");

        db.collection("School").document("0DKXnQhueh18DH7TSjsb").collection("User").document(fAuth.getUid()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "User added", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
