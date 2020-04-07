package com.example.authendance;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Random;

public class GenerateCode extends AppCompatActivity {

    private TextView codeField;
    private Button genCodeBtn;
    private ImageView qrCode;
    private CountDownTimer countDownTimer;
    private TextView countDownTimerText;
    private long timeLeftMilliseconds = 30000;
    private boolean timeRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        codeField = findViewById(R.id.codeField);
        genCodeBtn = findViewById(R.id.genCodeBtn);
        qrCode = findViewById(R.id.qrCode);
        countDownTimerText = findViewById(R.id.countDownTimerText);

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                int width = 200;
                int height = 200;

                codeField.setText(genRandString());
                String code = codeField.getText().toString();

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

                } catch (Exception e) {
                    e.printStackTrace();
                }

                startStop();
            }
        });
    }

    public void startStop() {
        if(timeRunning) {
            stopTimer();
        }
        else {
            startTimer();
        }
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilliseconds = millisUntilFinished;
                genCodeBtn.setEnabled(false);
                updateTimer();

            }

            @Override
            public void onFinish() {
                genCodeBtn.setEnabled(true);
            }
        }.start();

        timeRunning = true;
    }

    public void stopTimer() {
        countDownTimer.cancel();
        timeRunning = false;
    }

    public void updateTimer() {
        int minutes = (int) timeLeftMilliseconds / 30000;
        int seconds = (int) timeLeftMilliseconds % 30000 / 1000;

        String timeLeft = "" + minutes;
        timeLeft += ":";

        if(seconds < 10) {
            timeLeft += "0";
        }
        timeLeft += seconds;

        countDownTimerText.setText(timeLeft);
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
}
