package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.rpc.Code;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Locale;
import java.util.Objects;

public class CodeScreen extends AppCompatActivity {

    private static final String TAG = "CODE_SCREEN";

    private ImageView qrCode;
    private FirebaseFirestore db;
    private String code;
    private String moduleID;
    private long backPressed;
    private Toast backPressToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_screen);

        //Keeps screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db = FirebaseFirestore.getInstance();
        TextView codeField = findViewById(R.id.codeField);
        qrCode = findViewById(R.id.qrCode);
        Button finishBtn = findViewById(R.id.finishBtn);

        //Retrieves generated QR code text and document ID for module from the GenerateCode class
        Intent intent = getIntent();
        code = intent.getStringExtra("QR_CODE");
        moduleID = intent.getStringExtra("MOD_ID");
        codeField.setText(code);

        createQR();

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CodeScreen.this);
                builder.setTitle("Confirm Exit");
                builder.setMessage("Are you sure you want to exit? The code will be removed");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeQR();
                        finish();
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
        });
    }

    //Alerts the user the code will be reset when they press the back button
    @Override
    public void onBackPressed() {

        //Checks if back button was clicked twice within 2 seconds
        if(backPressed + 2000 > System.currentTimeMillis()) {
            backPressToast.cancel();
            super.onBackPressed();
            return;
        }
        else {
            backPressToast = Toast.makeText(CodeScreen.this, "Are you sure you want to exit? Code will be reset.", Toast.LENGTH_SHORT);
            backPressToast.show();
        }
        backPressed = System.currentTimeMillis();
    }

    private void createQR() {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 300;
        int height = 300;

        try {
            assert code != null;
            BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, width, height);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            qrCode.setImageBitmap(bitmap);
            qrCode.setVisibility(View.VISIBLE);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Removes QR code from database when activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeQR();

    }

    private void removeQR() {

        //Determines document path for the corresponding module
        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Modules")
                .document(moduleID);

        //Sets qr_code field to null
        documentReference.update("qr_code", null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Code successfully removed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Code not removed");
                    }
                });
    }
}
