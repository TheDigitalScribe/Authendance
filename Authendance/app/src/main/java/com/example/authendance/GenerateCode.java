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
import android.widget.Button;
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class GenerateCode extends AppCompatActivity {
    private static final long START_TIME_IN_MILLIS = 600000;
    private final static String TAG = "GENERATE_CODE";

    private TextView codeField;
    private Button genCodeBtn;
    private ImageView qrCode;

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;
    private FirebaseUser user;

    private TextView mTextViewCountDown;
    private long mTimeLeftInMilliseconds = START_TIME_IN_MILLIS;
    private long mEndTime;
    private boolean mTimerRunning;
    private boolean isClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        codeField = findViewById(R.id.codeField);
        genCodeBtn = findViewById(R.id.scanCodeBtn);
        qrCode = findViewById(R.id.qrCode);
        mTextViewCountDown = findViewById(R.id.countDownTimerText);

        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        qrCode.setVisibility(View.INVISIBLE);

        if(user != null) {
            Log.d(TAG, "User found");
        }
        else {
            Log.d(TAG, "User not found");
        }

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                int width = 200;
                int height = 200;

                isClicked = true;

                codeField.setText(genRandString());
                String code = codeField.getText().toString();

                //Log.d(TAG, Objects.requireNonNull(user.getEmail()));

                startTimer();

                //Creates and displays QR code
                try {
                    BitMatrix bitMatrix = qrCodeWriter.encode(code, BarcodeFormat.QR_CODE, width, height);
                    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                        }
                    }

                    qrCode.setImageBitmap(bitmap);

                    Map<String, Object> data = new HashMap<>();
                    data.put("qrcode", codeField.getText().toString());

                    DocumentReference codeRef = db.collection("School")
                            .document("0DKXnQhueh18DH7TSjsb")
                            .collection("User")
                            .document(Objects.requireNonNull(fAuth.getUid()));

                    codeRef.update("qrcode", codeField.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Code added");
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "Code not added");
                                }
                            });

                } catch (Exception e) {
                    e.printStackTrace();
                }
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
                genCodeBtn.setVisibility(View.INVISIBLE);
                mTimeLeftInMilliseconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                qrCode.setVisibility(View.INVISIBLE);
                genCodeBtn.setVisibility(View.VISIBLE);
                mTimeLeftInMilliseconds = START_TIME_IN_MILLIS;
                codeField.setText(null);
                updateTimer();

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

    //Method for generating random string for QR code generation
    private String genRandString() {
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 26; i++) {
            char c = chars[random.nextInt(chars.length)];
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private void removeQR() {
        DocumentReference codeRef = db.collection("School")
                .document("0DKXnQhueh18DH7TSjsb")
                .collection("User")
                .document(Objects.requireNonNull(fAuth.getUid()));

        codeRef.update("qrcode", null).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Code removed");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Code not removed");
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong("millisLeft", mTimeLeftInMilliseconds);
        outState.putBoolean("timerRunning", mTimerRunning);
        outState.putLong("endTime", mEndTime);

        if(isClicked) {
            outState.putString("codeField", codeField.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mTimeLeftInMilliseconds = savedInstanceState.getLong("millisLeft");
        mTimerRunning = savedInstanceState.getBoolean("timerRunning");
        codeField.setText(savedInstanceState.getString("codeField"));
        Log.d(TAG, codeField.getText().toString());
        updateTimer();

        if(mTimerRunning) {
            mEndTime = savedInstanceState.getLong("endTime");
            mTimeLeftInMilliseconds = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }
}
