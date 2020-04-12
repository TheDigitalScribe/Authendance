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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.zxing.Result;

import java.text.DateFormat;
import java.util.Calendar;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class CodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (checkPermission()) {
            Toast.makeText(CodeScanner.this, "Please scan the QR code now.", Toast.LENGTH_SHORT).show();
        } else {
            requestCameraPermission();
        }
    }

    //Checks if camera permission has already been granted
    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(CodeScanner.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

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
                            Intent intent = new Intent(CodeScanner.this, HomeScreen.class);
                            startActivity(intent);
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
        }
    }

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

    //Shows alert dialog when QR code is scanned
    @Override
    public void handleResult(Result result) {

        Intent codeIntent = getIntent();
        final String qrCode = codeIntent.getStringExtra("QR_CODE");

        //Shows the current date
        Calendar calendar = Calendar.getInstance();
        final String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        CollectionReference codeRef = db.collection("School").document("0DKXnQhueh18DH7TSjsb").collection("User");

        Query query = codeRef.whereEqualTo("qrcode", qrCode);

        assert qrCode != null;
        if (qrCode.matches(query.get().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Attendance Authenticated!");
            builder.setMessage("Your attendance is recorded for " + " <class> " + "on " + currentDate + ".");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(CodeScanner.this, HomeScreen.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Toast.makeText(CodeScanner.this, "QR code not valid", Toast.LENGTH_SHORT).show();
        }
    }
}