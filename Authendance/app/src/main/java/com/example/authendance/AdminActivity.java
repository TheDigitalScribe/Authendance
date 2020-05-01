package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity {

    TextView nameDisplay;
    TextView idDisplay;

    CardView studentsCard;
    CardView teachersCard;
    CardView attendanceCard;
    CardView settingsCard;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    private Spinner spinner;
    private Button submitBtn;

    private long backPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        nameDisplay = findViewById(R.id.nameDisplay);
        idDisplay = findViewById(R.id.idDisplay);

        studentsCard = findViewById(R.id.studentsCard);
        teachersCard = findViewById(R.id.teachersCard);
        attendanceCard = findViewById(R.id.attendanceCard);
        settingsCard = findViewById(R.id.settingsCard);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        getNameID();

        studentsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, StudentUsersList.class);
                startActivity(intent);
            }
        });

        teachersCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, TeacherUsersList.class);
                startActivity(intent);
            }
        });

        attendanceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Shows AlertDialog asking the user which module to search attendance records for
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                final View view = getLayoutInflater().inflate(R.layout.attendance_alertdialog, null);
                builder.setCustomTitle(view);
                spinner = view.findViewById(R.id.spinner);
                submitBtn = view.findViewById(R.id.submitBtn);

                populateSpinner();

                AlertDialog alert = builder.create();
                alert.show();

                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String spinnerValue = spinner.getSelectedItem().toString();

                        Intent intent = new Intent(AdminActivity.this, OverallAttendance.class);
                        intent.putExtra("MOD_ID", spinnerValue);
                        startActivity(intent);
                    }
                });
            }
        });


        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void populateSpinner() {

        //Prepares spinner and dropdown list
        final List<String> modulesList = new ArrayList<>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item, modulesList);
        adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        spinner.setAdapter(adapter);

        CollectionReference moduleRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Modules");

        moduleRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String moduleName = queryDocumentSnapshot.getId();
                        modulesList.add(moduleName);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    //Asks the user if they want to log out when they press the back button
    @Override
    public void onBackPressed() {

        if(backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
            builder.setTitle("Log Out");
            builder.setMessage("Do you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                    startActivity(intent);
                    fAuth.signOut();
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        backPressed = System.currentTimeMillis();
    }

    //Retrieves current user's name and ID from database and displays it
    private void getNameID() {
        String uid = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(uid);

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();

                    assert documentSnapshot != null;
                    if(documentSnapshot.exists()) {

                        String adminName = documentSnapshot.getString("name");
                        String adminID = documentSnapshot.getString("admin_id");

                        nameDisplay.setText(adminName);
                        idDisplay.setText(adminID);
                    }
                    else {
                        Toast.makeText(AdminActivity.this, "Document doesn't exist", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(AdminActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}
