package com.example.authendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Locale;
import java.util.Objects;

public class CodeScreen extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 30000;
    private static final String TAG = "CODE_SCREEN";
    private TextView codeField;
    private ImageView qrCode;

    private TextView mTextViewCountDown;
    private long mTimeLeftInMilliseconds = START_TIME_IN_MILLIS;
    private long mEndTime;
    private boolean mTimerRunning;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_screen);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            Log.d(TAG, "User found");
        }
        else {
            Log.d(TAG, "User not found");
        }

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        int width = 200;
        int height = 200;
        codeField = findViewById(R.id.codeField);
        qrCode = findViewById(R.id.qrCode);
        mTextViewCountDown = findViewById(R.id.countDownTimerText);

        //Retrieves generated QR code text from GenerateCode activity
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String code = data.getString("QR_CODE");

        codeField.setText(code);

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
            startTimer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeQR() {

        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        String docID = data.getString("DOC_ID");

        DocumentReference documentReference = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("Section")
                .document(docID);

        documentReference.update("qrcode", null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Qr code removed");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "qr code not removed");
                    }
                });
    }

    private void startTimer() {
        //mEndTime ensures timer is correct
        mEndTime = System.currentTimeMillis() + mTimeLeftInMilliseconds;
        CountDownTimer mCountDownTimer = new CountDownTimer(mTimeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                qrCode.setVisibility(View.VISIBLE);
                //genCodeBtn.setVisibility(View.INVISIBLE);
                mTimeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                qrCode.setVisibility(View.INVISIBLE);
                //genCodeBtn.setVisibility(View.VISIBLE);
                mTimeLeftInMilliseconds = START_TIME_IN_MILLIS;
                codeField.setText(null);
                //updateTimer();
                removeQR();
                finish();

                /*String qrCode = codeField.getText().toString();
                Intent studentIntent = new Intent(GenerateCode.this, CodeScanner.class);
                studentIntent.putExtra("QR_CODE", qrCode);
                startActivity(studentIntent);*/

                //Removes QR code text from Firestore
                //removeQR();

            }
        }
        .start();
        mTimerRunning = true;
    }

    private void updateTimer() {
        int minutes = (int) (mTimeLeftInMilliseconds / 1000) / 60;
        int seconds = (int) (mTimeLeftInMilliseconds / 1000) % 60;

        String timeLeft = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        mTextViewCountDown.setText(timeLeft);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("millisLeft", mTimeLeftInMilliseconds);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mEndTime);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTimeLeftInMilliseconds = savedInstanceState.getLong("millisLeft");
        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        codeField.setText(savedInstanceState.getString("codeField"));
        //Log.d(, codeField.getText().toString());
        updateTimer();

        if(mTimerRunning) {
            mEndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMilliseconds = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }

}
