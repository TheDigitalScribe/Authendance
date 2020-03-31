package com.example.qrcodetest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GenerateCode extends AppCompatActivity {

    private TextView codeField;
    private Button genCodeBtn;
    private ImageView qrCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_code);

        codeField = findViewById(R.id.codeField);
        genCodeBtn = findViewById(R.id.genCodeBtn);
        qrCode = findViewById(R.id.qrCode);

        genCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                int width = 200;
                int height = 200;

                private String generateString(int length) {
                    char[] characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
                    StringBuilder stringBuilder = new StringBuilder();
                }

                /*Makes phone keyboard disappear when button is clicked
                genEditText.onEditorAction(EditorInfo.IME_ACTION_DONE);*/

                //Creates and displays QR code
                try {
                    BitMatrix bitMatrix = qrCodeWriter.encode(textGen, BarcodeFormat.QR_CODE, width, height);
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
            }
        });
    }
}
