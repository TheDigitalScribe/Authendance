package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.Result;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

//This is the library that allows QR codes to be scanned
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class CodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private static final String TAG = "CODE_SCAN";

    private ZXingScannerView scannerView;
    private String moduleID;
    private String qrCode;
    private String studentID;
    private String currentDate;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();

        //Checks if camera permission was granted
        if (checkPermission()) {
            Toast.makeText(CodeScanner.this, "Please scan the QR code now.", Toast.LENGTH_SHORT).show();
        } else {
            requestCameraPermission();
        }

        //Gets current date
        currentDate = new SimpleDateFormat("dd MM YYYY", Locale.getDefault()).format(new Date());

        //Retrieves QR code, module and student ID from ModulePicker.class
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        assert data != null;
        qrCode = data.getString("QR_CODE");
        moduleID = data.getString("MOD_ID");
        studentID = data.getString("STU_ID");

    }

    //Checks if camera permission has already been granted
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(CodeScanner.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    //Requests camera permission using an AlertDialog
    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission for camera required.")
                    .setMessage("This permission is required to scan QR codes.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(CodeScanner.this, new String[]{CAMERA}, REQUEST_CAMERA);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
        }
    }

    //Checks if permission was granted already
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Authendance needs the camera to scan QR codes. Please enable camera permission in settings.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (checkPermission()) {
            if (scannerView == null) {
                scannerView = new ZXingScannerView(this);
                setContentView(scannerView);
            }
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {


        //If the QR code they scan matches the one the teacher generated
        if (qrCode.equals(result.getText())) {

            addAttendance();
            addAttendanceRecord();

            //Notifies the user their attendance is recorded
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
            builder.setTitle("Attendance Authenticated!");
            builder.setMessage("Your attendance for " + moduleID + " on " + currentDate + " has been recorded.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CodeScanner.this, StudentActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        } else {

            //Notifies the user the QR code is invalid
            AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
            builder.setTitle("Invalid QR code");
            builder.setMessage("This code is invalid.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CodeScanner.this, StudentActivity.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    //Adds attendance to the database collection where the teacher can see his students' attendance
    private void addAttendance() {

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Attendance")
                .document(moduleID)
                .collection("Date")
                .document(currentDate)
                .collection("Students")
                .document(studentID);

        documentReference.update("attended", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Success");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failure");
                    }
                });

    }

    //Adds attendance record to students' personal attendance
    private void addAttendanceRecord() {

        CollectionReference attendRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("AttendanceRecord")
                .document(studentID)
                .collection("Records");

        Query query = attendRef.whereEqualTo("module", moduleID).whereEqualTo("date", currentDate);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : Objects.requireNonNull(task.getResult())) {

                        String attRecord = queryDocumentSnapshot.getId();
                        Log.d(TAG, "attRecord: " + attRecord);

                        DocumentReference reference = db.collection("School")
                                .document("0DKXnQhueh18DH7TSjsb")
                                .collection("AttendanceRecord")
                                .document(studentID)
                                .collection("Records")
                                .document(attRecord);

                        reference.update("attended", true)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "Attendance updated");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Attendance failed to update");
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Error: " + task.getException());
                }
            }
        });

    }
}